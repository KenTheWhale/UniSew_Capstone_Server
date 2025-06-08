import '../../styles/auth/Home.css'

function RenderImageArea(){
    return(
        <div className="image-area">
            <img src={"/unisew.jpg"} alt="UniSew"/>
        </div>
    )
}

function RenderPage() {
    return (
        <div className="home-container">
            <RenderImageArea/>
        </div>
    )
}

export default function Home(){
    return (
        <RenderPage/>
    )
}