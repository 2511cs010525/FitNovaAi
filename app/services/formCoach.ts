import type {NormalizedLandmark, PoseLandmarker} from "@mediapipe/tasks-vision";

export type PoseAnalysis={score:number;corrections:string[];kneeAngle:number};

function angle(a:NormalizedLandmark,b:NormalizedLandmark,c:NormalizedLandmark){
  const ab={x:a.x-b.x,y:a.y-b.y},cb={x:c.x-b.x,y:c.y-b.y};
  const cosine=(ab.x*cb.x+ab.y*cb.y)/(Math.hypot(ab.x,ab.y)*Math.hypot(cb.x,cb.y));
  return Math.acos(Math.max(-1,Math.min(1,cosine)))*180/Math.PI;
}

export function analyzeSquat(points:NormalizedLandmark[]):PoseAnalysis{
  const left=angle(points[23],points[25],points[27]),right=angle(points[24],points[26],points[28]);
  const kneeAngle=(left+right)/2,symmetry=Math.abs(left-right),corrections:string[]=[];
  if(symmetry>16)corrections.push("Keep both knees moving evenly");
  if(kneeAngle<65)corrections.push("Reduce depth and stay controlled");
  const shoulderTilt=Math.abs(points[11].y-points[12].y);
  if(shoulderTilt>.07)corrections.push("Keep your shoulders level");
  const visibility=[11,12,23,24,25,26,27,28].reduce((sum,i)=>sum+(points[i].visibility??1),0)/8;
  if(visibility<.65)corrections.push("Step back so your full body is visible");
  const score=Math.round(Math.max(0,Math.min(100,100-symmetry*1.2-shoulderTilt*180-(1-visibility)*35)));
  return {score,corrections:corrections.length?corrections:["Good alignment — keep moving with control"],kneeAngle};
}

export async function createPoseLandmarker():Promise<PoseLandmarker>{
  const {FilesetResolver,PoseLandmarker}=await import("@mediapipe/tasks-vision");
  const vision=await FilesetResolver.forVisionTasks("/mediapipe/wasm");
  return PoseLandmarker.createFromOptions(vision,{baseOptions:{modelAssetPath:"/models/pose_landmarker_lite.task",delegate:"GPU"},runningMode:"VIDEO",numPoses:1,minPoseDetectionConfidence:.55,minPosePresenceConfidence:.55,minTrackingConfidence:.55});
}

export function drawPose(canvas:HTMLCanvasElement,video:HTMLVideoElement,points:NormalizedLandmark[],connections:{start:number;end:number}[]){
  canvas.width=video.videoWidth;canvas.height=video.videoHeight;
  const context=canvas.getContext("2d");if(!context)return;
  context.clearRect(0,0,canvas.width,canvas.height);context.strokeStyle="#9cc3a7";context.lineWidth=4;context.fillStyle="#f1f4ef";
  for(const connection of connections){const a=points[connection.start],b=points[connection.end];if((a.visibility??1)<.45||(b.visibility??1)<.45)continue;context.beginPath();context.moveTo(a.x*canvas.width,a.y*canvas.height);context.lineTo(b.x*canvas.width,b.y*canvas.height);context.stroke()}
  for(const point of points){if((point.visibility??1)<.45)continue;context.beginPath();context.arc(point.x*canvas.width,point.y*canvas.height,4,0,Math.PI*2);context.fill()}
}
