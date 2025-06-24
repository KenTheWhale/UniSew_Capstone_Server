import {Dashboard, Draw, AccountCircle} from '@mui/icons-material';
import DashboardUILayout from "../ui/DashboardUILayout.jsx";

function RenderLayout() {

    const navigation = [
        {
            kind: 'header',
            title: 'Dashboard',
        },
        {
            segment: 'designer/dashboard',
            title: 'Dashboard',
            icon: <Dashboard/>
        },
        /*{
            kind: 'header',
            title: 'Design Management',
        },
        {
            segment: 'designer/design',
            title: 'Design Request',
            icon: <Draw/>
        },*/
        {
            kind: 'header',
            title: 'Account Settings',
        },
        {
            segment: 'designer/profile',
            title: 'Profile',
            icon: <AccountCircle/>
        }
    ]


    return (
        <DashboardUILayout
            navigation={navigation}
            title={'Dashboard'}
            header={'Designer Dashboard'}
        />
    )

}

export default function DesignerLayout() {
    return (
        <RenderLayout/>
    )
}