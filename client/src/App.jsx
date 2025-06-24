import './styles/App.css'
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SnackbarProvider} from 'notistack'
import {Grow} from "@mui/material";
import WebAppUILayout from "./layouts/ui/WebAppUILayout.jsx";
import Home from "./components/auth/Home.jsx";
import SignIn from "./components/auth/SignIn.jsx";
import Register from "./components/auth/SignUp.jsx";
import {useEffect} from "react";
import UniSewConsole from "./components/ui/UniSewConsole.jsx";
import 'bootstrap/dist/css/bootstrap.min.css'
import PlatformAdminLayout from "./layouts/platform_admin/PlatformAdminLayout.jsx";
import PlatformAdminDashboard from "./components/platform_admin/PlatformAdminDashboard.jsx";
import PlatformDesignRequestLayout from "./layouts/platform_design_request/PlatformDesignRequestLayout.jsx";
import DesignerDetail from "./components/platform_design_request/designers/DesignerDetail.jsx";
import AccountManagement from "./components/platform_admin/AccountManagement.jsx";


const router = createBrowserRouter([
    {
        path: "/home",
        element: (
            <WebAppUILayout title={"Home"}>
                <Home/>
            </WebAppUILayout>
        )
    },
    {
        path: "/sign-in",
        element: (
            <WebAppUILayout title={"Sign in"}>
                <SignIn/>
            </WebAppUILayout>
        )
    },
    {
        path: "/sign-up",
        element: (
            <WebAppUILayout title={"Sign up"}>
                <Register/>
            </WebAppUILayout>
        )
    },
    {
        path: "/admin",
        element: <PlatformAdminLayout/>,
        children: [
            {
                index: true,
                element: <Navigate to={'/admin/dashboard'}/>
            },
            {
                path: 'dashboard',
                element: <PlatformAdminDashboard/>
            },
            {
                path: 'accounts',
                element: <AccountManagement></AccountManagement>
            }
        ]
    },

    {
        path: "/design",
        element: (
            <WebAppUILayout title={"Choose Designer"}>
                <PlatformDesignRequestLayout></PlatformDesignRequestLayout>
            </WebAppUILayout>
        )

    },

    {
        path: "/designDetail",
        element:
        <WebAppUILayout title={"Designer Detail"}>
            <DesignerDetail></DesignerDetail>
        </WebAppUILayout>

    },


    {
        path: "*",
        element: <Navigate to={"/home"}/>
    }
])

function App() {

    useEffect(() => {
        UniSewConsole()
    }, []);

    return (
        <SnackbarProvider
            maxSnack={4}
            anchorOrigin={{vertical: 'top', horizontal: 'right'}}
            autoHideDuration={1500}
            TransitionComponent={Grow}>
            <RouterProvider router={router}/>
        </SnackbarProvider>
    )
}

export default App
