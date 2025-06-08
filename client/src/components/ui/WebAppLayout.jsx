import '../../styles/ui/WebAppLayout.css'
import {Button, Divider, Link, Typography} from "@mui/material";
import {useNavigate} from "react-router-dom";

function RenderFooterPolicyButton({link, title}) {
    const navigate = useNavigate()

    return(
        <Link
            component={"button"}
            variant={"body2"}
            underline={"none"}
            color={"textPrimary"}
            onClick={() => navigate(link)}
        >
            {title}
        </Link>
    )
}

function RenderHeaderButton({link, title}) {
    const navigate = useNavigate()

    return(
        <Link
            className={"btn-link"}
            component={"button"}
            underline={"none"}
            color={"textPrimary"}
            sx={{fontWeight: "500", fontSize: '1.2rem', height: '100%', width: '7vw'}}
            onClick={() => navigate(link)}
        >
            {title}
        </Link>
    )
}

function RenderHeader() {
    const navigate = useNavigate()
    return (
        <div className="header">
            <img src={"/logo_full.png"} alt="UniSew"/>
            <div className="header-content">
                <RenderHeaderButton link={"/home"} title="Home"/>
                <RenderHeaderButton link={"/info/about"} title="About"/>
                <RenderHeaderButton link={"/info/contact"} title="Contact"/>
                <RenderHeaderButton link={"/info/showroom"} title="Showroom"/>
            </div>
            <div className="header-buttons">
                <Button variant={"outlined"} onClick={() => navigate("/sign-up")}>Sign up</Button>
                <Button variant={"contained"} onClick={() => navigate("/sign-in")}>Sign in</Button>
            </div>
        </div>
    )
}

function RenderFooter() {
    const today = new Date();

    return (
        <div className={"footer"}>
            <div className={"footer-content"}>
                <img src={"/logo_full.png"} alt="UniSew"/>
                <div className={"footer-item"}>
                    <Typography variant={"body1"} sx={{fontWeight: "bold", color: 'black'}}>About UniSew</Typography>
                    <RenderFooterPolicyButton title={"About us"} link={"/info/about"}/>
                    <RenderFooterPolicyButton title={"Contact us"} link={"/info/contact"}/>
                </div>
                <div className={"footer-item"}>
                    <Typography variant={"body1"} sx={{fontWeight: "bold", color: 'black'}}>Resources</Typography>
                    <RenderFooterPolicyButton title={"Order policy"} link={"/policy/order"}/>
                    <RenderFooterPolicyButton title={"Return policy"} link={"/policy/return"}/>
                    <RenderFooterPolicyButton title={"Privacy policy"} link={"/policy/privacy"}/>
                    <RenderFooterPolicyButton title={"Terms of service"} link={"/policy/term"}/>
                </div>
                <div className={"footer-item"}>
                    <Typography variant={"body1"} sx={{fontWeight: "bold", color: 'black'}}>Payment</Typography>
                    <img src={"/payos.png"} alt="PayOS"/>

                    <Typography variant={"body1"} sx={{fontWeight: "bold", color: 'black', marginTop: "2vh"}}>Shipping</Typography>
                    <img src={"/ghn.png"} alt="Giao hang nhanh"/>
                </div>
            </div>
            <Divider/>
            <div className={"footer-right"}>
                <span>&copy; {today.getFullYear()} UniSew, powered by UniSew team, All rights reserved.</span>
            </div>
        </div>
    )
}

function RenderPage({children, title}) {
    document.title = title;
    return (
        <div className={"main"}>
            <RenderHeader/>
            <div className={"body"}>
                {children}
            </div>
            <RenderFooter/>
        </div>
    )
}

export default function WebAppLayout({children, title}) {
    return (
        <RenderPage children={children} title={title}/>
    )
}