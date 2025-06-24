import {
    Box,
    Button,
    Card,
    CardActionArea,
    CardActions,
    CardContent,
    CardMedia, Divider,
    Grid, Icon,
    Stack, Tooltip,
    Typography
} from "@mui/material";
import DashboardBox from "./components/DashboardBox.jsx";
import PersonIcon from '@mui/icons-material/Person';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import StarIcon from '@mui/icons-material/Star';
import DashboardChart from "./components/DashboardChart.jsx";


 const PlatformAdminDashboard = () => {
    return  (
        <>
            <div className="right-content w-100 ">
                <div className="row dashboardBoxWrapperRow">
                    <div className="col-md-9">
                        <div className="dashboardBoxWrapper d-flex">
                            <DashboardBox color={["#1da256","#48d483"]} icon={<PersonIcon/>} grow={true}></DashboardBox>
                            <DashboardBox color={["#c012e2","#eb64fe"]} icon={<ShoppingCartIcon/>}></DashboardBox>
                            <DashboardBox color={["#2c78e5","#60aff5"]} icon={<ShoppingBagIcon/>}></DashboardBox>
                            <DashboardBox color={["#e1950e","#f3cd29"]} icon={<StarIcon/>}></DashboardBox>
                        </div>
                    </div>

                    <div className="col-md-3">
                        <div className="box w-100" style={{ height: '100%' }}>
                            <DashboardChart />
                        </div>
                    </div>


                </div>
            </div>
        </>


    )



}
export default PlatformAdminDashboard;