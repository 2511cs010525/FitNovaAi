export type UserView={id:number;name:string;email:string;onboardingComplete:boolean};
export type AuthResponse={token:string;user:UserView};
export type ReadinessView={id:number;date:string;score:number;status:string;recommendation:string;availableMinutes:number};
export type WorkoutView={id:number;title:string;durationMinutes:number;intensity:string;location:string;status:string;exercises:string[];feedback?:string;startedAt?:string;completedAt?:string};
export type DashboardView={user:UserView;readiness:ReadinessView|null;todayWorkout:WorkoutView|null;summary:{completedWorkouts:number;trainingMinutes:number;streak:number;averageFormScore:number|null};recentActivity:WorkoutView[]};
export type ProfileView={age:number|null;heightCm:number|null;weightKg:number|null;goal:string|null;experience:string|null;location:string|null;equipment:string[];diet:string|null;allergies:string|null;limitations:string|null;onboardingComplete:boolean};
export type MealView={id:number;name:string;goal:string;ingredients:string[];instructions:string[];calories:number;proteinG:number;prepMinutes:number;createdAt:string};
export type FormSessionView={id:number;exercise:string;reps:number;score:number;corrections:string[];createdAt:string};

const API_BASE=(process.env.NEXT_PUBLIC_API_BASE_URL||"http://localhost:8080/api").replace(/\/$/,"");
const TOKEN_KEY="fitnova_token";
export const session={token:()=>typeof window==="undefined"?null:localStorage.getItem(TOKEN_KEY),save:(value:AuthResponse)=>{localStorage.setItem(TOKEN_KEY,value.token);localStorage.setItem("fitnova_user",JSON.stringify(value.user));},clear:()=>{localStorage.removeItem(TOKEN_KEY);localStorage.removeItem("fitnova_user");}};

class ApiError extends Error{constructor(public status:number,message:string){super(message)}}
async function request<T>(path:string,init:RequestInit={}):Promise<T>{const token=session.token();const response=await fetch(`${API_BASE}${path}`,{...init,headers:{"Content-Type":"application/json",...(token?{Authorization:`Bearer ${token}`}:{}) ,...init.headers}});if(response.status===401){session.clear();if(typeof window!=="undefined"&&!location.pathname.startsWith("/login"))location.assign("/login");}if(!response.ok){let message="FitNova could not complete that request.";try{message=(await response.json()).message||message}catch{void 0}throw new ApiError(response.status,message)}if(response.status===204)return undefined as T;return response.json()}
const json=(method:string,body?:unknown):RequestInit=>({method,body:body===undefined?undefined:JSON.stringify(body)});
export const authApi={signup:(body:{fullName:string;email:string;password:string})=>request<AuthResponse>("/auth/signup",json("POST",body)),login:(body:{email:string;password:string})=>request<AuthResponse>("/auth/login",json("POST",body))};
export const profileApi={get:()=>request<ProfileView>("/profile"),save:(body:unknown)=>request<ProfileView>("/profile",json("PUT",body))};
export const readinessApi={latest:()=>request<ReadinessView|null>("/readiness/latest"),create:(body:unknown)=>request<ReadinessView>("/readiness",json("POST",body))};
export const workoutApi={list:()=>request<WorkoutView[]>("/workouts"),generate:(body:unknown)=>request<WorkoutView>("/workouts/generate",json("POST",body)),start:(id:number)=>request<WorkoutView>(`/workouts/${id}/start`,json("PATCH")),complete:(id:number,body:unknown)=>request<WorkoutView>(`/workouts/${id}/complete`,json("PATCH",body))};
export const dashboardApi={get:()=>request<DashboardView>("/dashboard")};
export const mealApi={list:()=>request<MealView[]>("/meals"),save:(body:unknown)=>request<MealView>("/meals",json("POST",body))};
export const aiApi={status:()=>request<{configured:boolean;model:string}>("/ai/status"),ask:(message:string)=>request<{answer:string}>("/ai/ask",json("POST",{message})),kitchen:(ingredients:string[],goal:string)=>request<MealView>("/ai/kitchen",json("POST",{ingredients,goal}))};
export const formApi={list:()=>request<FormSessionView[]>("/form-sessions"),save:(body:unknown)=>request<FormSessionView>("/form-sessions",json("POST",body))};
export const progressApi={get:()=>request<unknown>("/progress")};
