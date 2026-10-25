"use client";

import { useRouter } from "next/navigation";
import { FitNovaApp } from "../FitNovaApp";

export default function DashboardPage(){const router=useRouter();return <FitNovaApp onExit={()=>router.push("/")}/>}
