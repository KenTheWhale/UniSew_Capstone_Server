import '../../styles/auth/SignIn.css'
import {Button, Typography, Link} from "@mui/material";
import {KeyboardBackspace} from '@mui/icons-material';
import {getGoogleUrl} from "../../services/AuthService.jsx";
import {enqueueSnackbar} from "notistack";

const signIn = async () => {
    const response = await getGoogleUrl()
    if(response.status === 200 && response.data.data.url){
        window.location.href = response.data.data.url
    }
}

function RenderLoginArea() {
    return (
        <div className={'sign-in-login-area-container'}>
            <Typography variant={"h2"}>UniSew</Typography>
            <Typography variant={"h6"}>Sign in to your account</Typography>
            <Button
                fullWidth
                size={'medium'}
                startIcon={<img src={'/google.png'} alt={'Google'} height={20} width={20}/>}
                variant={"outlined"}
                color={"inherit"}
                onClick={signIn}
            >
                Continue with Google
            </Button>
            <div className={'d-flex align-items-end'}>
                <KeyboardBackspace height={15} width={15} sx={{marginRight: '0.5vw'}}/>
                <Link sx={{color: 'white', cursor: 'pointer'}} underline={"none"} href={'/home'}>Back to home</Link>
            </div>

        </div>
    )
}

function RenderPage() {

    if(localStorage.length > 0) {
        localStorage.clear()
        enqueueSnackbar("Logout successfully, you will be redirect to homepage in seconds", {variant: 'success'})
        setTimeout(() => {
            window.location.href = '/home'
        }, 2000)
    }

    return (
        <div className={'sign-in-main'}>
            <div className={'sign-in-main-container'}>
                <div className={'sign-in-login-area'}>
                    <RenderLoginArea/>
                </div>
                <div className={'sign-in-img-area'}>
                    <img src={"/unisew.jpg"} alt={'UniSew'}/>
                </div>
            </div>
        </div>
    )
}

export default function SignIn() {
    document.title = "Sign in"
    return (
        <RenderPage/>
    )
}