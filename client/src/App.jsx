import './styles/App.css'
import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import {SnackbarProvider} from 'notistack'
import {Grow} from "@mui/material";
import WebAppLayout from "./components/ui/WebAppLayout.jsx";
import Home from "./components/auth/Home.jsx";
import SignIn from "./components/auth/SignIn.jsx";
import Register from "./components/auth/SignUp.jsx";
import {useEffect} from "react";
import UniSewConsole from "./components/deco/UniSewConsole.jsx";
import 'bootstrap/dist/css/bootstrap.min.css'

const router = createBrowserRouter([
    {
        path: "/home",
        element: (
            <WebAppLayout title={"Home"}>
                <Home/>
            </WebAppLayout>
        )
    },
    {
        path: "/sign-in",
        element: (
            <WebAppLayout title={"Sign in"}>
                <SignIn/>
            </WebAppLayout>
        )
    },
    {
        path: "/sign-up",
        element: (
            <WebAppLayout title={"Sign up"}>
                <Register/>
            </WebAppLayout>
        )
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
