import '../../styles/ui/DashBoardWebUILayout.css'
import {Outlet} from 'react-router-dom'
import {Link, Typography} from "@mui/material";

function RenderNav({navigation}) {

    function RenderNavCaption({label, key}) {
        return (
            <Typography key={key} variant={"h6"} sx={{marginTop: '2vh'}}>{label}</Typography>
        )
    }

    function RenderNavSub({label, segment, key}) {
        return (
            <Link
                key={key}
                href={segment}
                variant={"body2"}
                underline={"none"}
                color={"textPrimary"}
                sx={
                    {
                        marginLeft: "2vw",
                        cursor: 'pointer'
                    }
                }
            >
                {label}
            </Link>
        )
    }

    return (
        <div>
            {
                navigation.map((el, index) => {
                    if (el.type === 'caption') {
                        return (
                            <RenderNavCaption label={el.label} key={index}/>
                        )
                    } else {
                        return (
                            <RenderNavSub label={el.label} segment={el.segment} key={index}/>
                        )
                    }
                })
            }
        </div>
    )
}

function RenderPage({navigation}) {
    return (
        <div className={'dbwui-main'}>
            <div className={'dbwui-nav'}>
                <RenderNav navigation={navigation}/>
            </div>
            <div className={'dbwui-outlet'}>
                <Outlet/>
            </div>
        </div>
    )
}

export default function DashboardWebUILayout({navigation}) {
    return (
        <RenderPage navigation={navigation}/>
    )
}