import {useLocation} from "react-router-dom";
import axios from "axios";
import {useEffect, useState} from "react";
import {login} from "../../services/AuthService.jsx";
import {enqueueSnackbar} from "notistack";

async function Login(email, avatar, name) {
    const response = await login(email, avatar, name)
    if (response) {
        return response
    }
}

function HandleResponse() {
    const [googleResponse, setGoogleResponse] = useState(null)
    const location = useLocation()
    const accessToken = new URLSearchParams(location.hash.substring(1)).get('access_token')

    useEffect(() => {
        async function FetchInfo() {
            const r = await axios.get("https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken)
            if (r.status === 200) {
                return r
            }
        }

        FetchInfo().then(r => setGoogleResponse(r))
    }, [accessToken]);

    if (googleResponse) {
        Login(googleResponse.data.email, googleResponse.data.picture, googleResponse.data.name).then(res => {
            if (res.status === 200) {
                localStorage.setItem("user", JSON.stringify(res.data.data))
                localStorage.setItem("message", res.data.message)
                localStorage.setItem("variant", "success")
                switch (res.data.data.role) {
                    case "admin":
                        window.location.href = '/admin/dashboard'
                        break;
                    case "school":
                        window.location.href = '/home'
                        break;
                    case "designer":
                        window.location.href = '/designer/dashboard'
                        break;
                    case "garment":
                        window.location.href = '/garment/dashboard'
                        break;
                    default:
                        window.location.href = '/sign-in'
                        break;
                }
            }
        }).catch(err => {
            enqueueSnackbar(err.response.data.message, {variant: "error"})
            setTimeout(() => {
                window.location.href = '/sign-in'
            }, 1500)
        })
    }
    return (
        <></>
    )
}

export default function GoogleResponse() {
    return <HandleResponse/>
}