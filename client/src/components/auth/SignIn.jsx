import '../../styles/auth/SignIn.css'
import {SignInPage} from "@toolpad/core";
import {ReactRouterAppProvider} from "@toolpad/core/react-router";
import {
    Button, FormControl,
    IconButton,
    InputAdornment,
    InputLabel,
    Link,
    OutlinedInput,
    TextField,
    Typography
} from "@mui/material";
import {useState} from "react";
import {Visibility, VisibilityOff} from '@mui/icons-material';

/* init ui */
const brand = {
    logo: (
        <img
            src="/logo.png"
            alt="UniSew"
            width={50}
            height={50}
        />
    ),
    title: 'UniSew',
}

const providers = [
    { id: 'google', name: 'Google' },
    { id: 'credentials', name: 'Email and Password'}
]

function CustomSignInBtn(){
    return (
        <Button
            variant={"contained"}
            size={"medium"}
            fullWidth
            sx={{
                margin: '2vh 0'
            }}
        >
            Sign in
        </Button>
    )
}

function CustomSubtitle(){
    return (
        <Typography variant={"body2"} sx={{marginBottom: '2vh'}}>UniSew</Typography>
    )
}

function CustomTitle(){
    return (
        <Typography variant={"h4"} sx={{marginBottom: '0.5vh'}}>SIGN IN</Typography>
    )
}

function CustomRegister() {
    return (
        <Link href="/client/src/components/auth/SignUp" variant="body2" underline="none">
            Sign up
        </Link>
    );
}

function CustomForgetPass(){

    return (
        <Link underline={"none"} variant={"body2"} href={"/home"}>Forget password ?</Link>
    )
}

function CustomEmailField() {
    return (
        <TextField
            label="Email *"
            name="email"
            type="email"
            size="small"
            fullWidth
            variant="outlined"
        />
    );
}

function CustomPasswordField() {
    const [showPassword, setShowPassword] = useState(false);

    const handleClickShowPassword = () => setShowPassword((show) => !show);

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    return (
        <FormControl sx={{ my: 2 }} fullWidth variant="outlined">
            <InputLabel size="small">
                Password *
            </InputLabel>
            <OutlinedInput
                type={showPassword ? 'text' : 'password'}
                name="password"
                size="small"
                endAdornment={
                    <InputAdornment position="end">
                        <IconButton
                            onClick={handleClickShowPassword}
                            onMouseDown={handleMouseDownPassword}
                            edge="end"
                            size="small"
                        >
                            {showPassword ? (
                                <VisibilityOff fontSize="inherit" />
                            ) : (
                                <Visibility fontSize="inherit" />
                            )}
                        </IconButton>
                    </InputAdornment>
                }
                label="Password"
            />
        </FormControl>
    );
}


/* end init ui */

function RenderPage() {

    return (
        <div className="sign-in-container">
            <SignInPage
                providers={providers}
                slots={{
                    submitButton: CustomSignInBtn,
                    subtitle: CustomSubtitle,
                    title: CustomTitle,
                    forgotPasswordLink: CustomForgetPass,
                    signUpLink: CustomRegister,
                    emailField: CustomEmailField,
                    passwordField: CustomPasswordField,
                }}
            />
        </div>
    )
}

export default function SignIn() {

    return (
        <ReactRouterAppProvider branding={brand}>
            <RenderPage/>
        </ReactRouterAppProvider>
    )
}