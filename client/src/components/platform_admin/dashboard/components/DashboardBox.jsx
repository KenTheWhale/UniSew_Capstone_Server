import { useEffect, useState } from "react";
import { Button, MenuItem, Menu, IconButton } from "@mui/material";
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import MoreVertIcon from '@mui/icons-material/MoreVert';

import TimerIcon from '@mui/icons-material/Timer';
const DashboardBox = (props) => {
    const ITEM_HEIGHT = 48;
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);

    const handleClick = (event) => setAnchorEl(event.currentTarget);
    const handleClose = () => setAnchorEl(null);

    useEffect(() => {
        console.log(props.color);
    }, );

    return (
        <Button className="dashboardBox" style={{
            backgroundImage: `linear-gradient(to right, ${props.color?.[0]} , ${props.color?.[1]})`
        }}>
            <span className="chart">
                {props.grow ? <TrendingUpIcon /> : <TrendingDownIcon />}
            </span>

            <div className="d-flex w-100 justify-content-between">
                <div className="col1">
                    <h4>Total Users</h4>
                    <span>277</span>
                </div>
                {props.icon && (
                    <div className="icon-top-right">
                        {props.icon}
                    </div>
                )}
            </div>

            <div className="d-flex align-items-center justify-content-between w-100 bottomEle">
                <h6 className="mb-0 mt-0 text-white">Last Month</h6>
                <IconButton
                    className="toggleIcon"
                    aria-label="more"
                    onClick={handleClick}
                >
                    <MoreVertIcon />
                </IconButton>
            </div>

            <Menu
                id="long-menu"
                MenuListProps={{ 'aria-labelledby': 'long-button' }}
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                PaperProps={{
                    style: {
                        maxHeight: ITEM_HEIGHT * 4.5,
                        width: '20ch',
                    },
                }}
            >
                {["Last Day", "Last Week", "Last Month", "Last Year"].map((label, idx) => (
                    <MenuItem key={idx} onClick={handleClose}>
                        <TimerIcon /> &nbsp; {label}
                    </MenuItem>
                ))}
            </Menu>
        </Button>
    );
}

export default DashboardBox;