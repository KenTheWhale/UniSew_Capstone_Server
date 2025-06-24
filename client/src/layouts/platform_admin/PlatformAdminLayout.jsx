import {Dashboard, ManageAccounts, ShoppingCart} from '@mui/icons-material';
import DashboardUILayout from "../ui/DashboardUILayout.jsx";
import {Account} from "@toolpad/core";
import AccountManagement from "../../components/platform_admin/account/AccountManagement.jsx";

function RenderLayout() {

    const navigation = [
        {
            kind: 'header',
            title: 'Dashboard',
        },
        {
            segment: 'admin/dashboard',
            title: 'Dashboard',
            icon: <Dashboard/>
        },
        {
            kind: 'header',
            title: 'Account Management',
        },
        {
            segment: 'admin/accounts',
            title: 'Accounts',
            icon: <ManageAccounts/>,

        },
        {
            kind: 'header',
            title: 'Order Management',
        },
        {
            segment: 'admin/orders',
            title: 'Orders',
            icon: <ShoppingCart/>
        },
    ]


    return (
        <DashboardUILayout
            navigation={navigation}
            title={'Dashboard'}
            header={'Admin Dashboard'}
        />
    )

}

export default function PlatformAdminLayout() {
    return (
        <RenderLayout/>
    )
}