import React, {useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import '../../styles/school/DesignerDetail.css';
import {
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Typography,
    Box,
    ListItemText,
    CircularProgress,
    ListItem,
    List,
    IconButton,
    ListItemSecondaryAction,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody
} from "@mui/material";
import {chooseDesignPackage, getClothByRequestId, viewListHistory} from "../../services/DesignService.jsx";
import VisibilityIcon from "@mui/icons-material/Visibility";

export default function DesignerDetail() {

    const [selectedPackage, setSelectedPackage] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);
    const [openRequestDialog, setOpenRequestDialog] = useState(false);
    const [requestList, setRequestList] = useState([]);
    const [loadingRequests, setLoadingRequests] = useState(false);
    const [openDetailDialog, setOpenDetailDialog] = useState(false);
    const [clothList, setClothList] = useState([]);
    const [selectedRequestId, setSelectedRequestId] = useState(null);
    const navigate = useNavigate();

    const location = useLocation();
    const designer = location.state?.designer;


    if (!designer) {
        return <div style={{padding: 40}}>No designer data found.</div>;
    }
    const handleSelectPackage = (pkg) => {
        setSelectedPackage(pkg);
        setOpenDialog(true);
    };

    const handleConfirmPay = async () => {
        setLoadingRequests(true);
        setOpenRequestDialog(true);
        try {
            const res = await viewListHistory();
            const createdRequests = res.data.filter(item => item.status === "CREATED" && item.package == null);
            setRequestList(createdRequests);
        } catch (error) {
            console.error("Failed to fetch requests:", error);
        } finally {
            setLoadingRequests(false);
        }
    };

    const handleViewRequestDetail = async (requestId) => {
        try {
            setSelectedRequestId(requestId);
            const res = await getClothByRequestId(requestId);
            setClothList(res);
            setOpenDetailDialog(true);
        } catch (err) {
            console.error("Failed to fetch cloths", err);
        }
    };

    const handleChooseRequest = async () => {
        try {
            const res = await chooseDesignPackage({
                designRequestId: selectedRequestId,
                packageId: selectedPackage.id
            });

            console.log("Package selected successfully:", res);
            setOpenDetailDialog(false);
            setOpenDialog(false);
            alert("You have successfully selected a package!");
        } catch (error) {
            console.error("Error choosing package:", error);
            alert("Failed to choose package");
        }
    };

    const handleCreateRequest = () => {
        localStorage.setItem("createDesignPopup", "open")
        localStorage.setItem("pageInfo", JSON.stringify({designer: designer, url: "/designer-profile"}))
        navigate("/school");
    }


    return (
        <div className="profile-page bg-light pb-5">
            <div
                className="profile-banner"
                style={{
                    backgroundImage: `url(https://png.pngtree.com/thumb_back/fh260/background/20221011/pngtree-blue-gold-background-banner-idea-modern-simple-free-download-image_1467602.jpg)`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    height: 240,
                    position: "relative"
                }}
            >
                <div className="profile-avatar-box"
                     style={{position: "absolute", left: 40, bottom: -50, display: "flex", alignItems: "center"}}>
                    <img
                        src={designer.profile.avatar}
                        alt="Avatar"
                        className="rounded-circle border border-4 border-white shadow"
                        style={{width: 100, height: 100, objectFit: "cover"}}
                    />
                    <div className="ms-4 d-none d-md-block">
                        <h2 className="fw-bold mb-1">{designer.profile.name}</h2>
                        <div className="text-muted mb-1">Graphic Designer</div>
                        <div className="d-flex align-items-center gap-3">
                            <span><i className="bi bi-geo-alt-fill me-1"></i>Vietnam</span>
                            <span className="text-muted">Joined 2022</span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="container" style={{marginTop: 70}}>
                <div className="row">
                    <div className="col-lg-8">
                        <div className="mb-4 d-flex gap-3">
                            <button className="btn btn-success rounded-pill fw-semibold px-4">
                                Contact
                            </button>
                        </div>

                        <div className="mb-4">
                            <h5 className="fw-bold mb-2">About</h5>
                            <p className="text-dark">{designer.bio}</p>
                        </div>
                        <div className="mb-4">
                            <h5 className="fw-bold mb-2">My Portfolio</h5>
                        </div>

                        <div className="mb-4">
                            <h5 className="fw-bold mb-3">Packages</h5>
                            <div className="row g-3">
                                {designer.package?.map(pkg => (
                                    <div className="col-md-6" key={pkg.id}>
                                        <div className="card h-100 shadow-sm">
                                            <div className="card-body">
                                                <h6 className="card-title fw-semibold">{pkg.name}</h6>
                                                <div className="text-muted small mb-2">{pkg.header_content}</div>
                                                <div><strong>Fee:</strong> ${pkg.fee}</div>
                                                <div><strong>Delivery:</strong> {pkg.delivery_duration} days</div>
                                                <div><strong>Revisions:</strong> {pkg.revision_time}</div>
                                            </div>
                                            <div className="card-footer d-flex justify-content-center">
                                                <Button variant="outlined" onClick={() => handleSelectPackage(pkg)}>
                                                    Select
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="text-muted">/* Reviews section /</div>
                    </div>

                    <div className="col-lg-4 mt-4 mt-lg-0">
                        <div className="card p-4 shadow-sm mb-4">
                            <h6 className="fw-bold mb-3">Profile Stats</h6>
                            <div className="d-flex justify-content-between mb-2">
                                <span>Orders Completed</span>
                                <span className="fw-semibold text-success">98%</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span>Response Time</span>
                                <span>1 Hour</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span>Delivered on Time</span>
                                <span>On Time</span>
                            </div>
                            <div className="d-flex justify-content-between">
                                <span>Repeat Clients</span>
                                <span>21%</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Confirm Your Package</DialogTitle>
                <DialogContent dividers>
                    {selectedPackage && (
                        <Box>
                            <Typography variant="h6">{selectedPackage.name}</Typography>
                            <Typography variant="body2" gutterBottom color="text.secondary">
                                {selectedPackage.header_content}
                            </Typography>
                            <Typography><strong>Fee:</strong> ${selectedPackage.fee}</Typography>
                            <Typography><strong>Delivery:</strong> {selectedPackage.delivery_duration} days</Typography>
                            <Typography><strong>Revisions:</strong> {selectedPackage.revision_time}</Typography>
                        </Box>
                    )}
                </DialogContent>
                <DialogActions sx={{justifyContent: "space-between", px: 3}}>
                    <Typography variant="h6" fontWeight="bold">
                        Total: ${selectedPackage?.fee || 0}
                    </Typography>
                    <Button variant="contained" onClick={handleConfirmPay}>
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={openRequestDialog} onClose={() => setOpenRequestDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Select a Design Request</DialogTitle>
                <DialogContent dividers>
                    {loadingRequests ? (
                        <Box display="flex" justifyContent="center" alignItems="center" p={2}>
                            <CircularProgress/>
                        </Box>
                    ) : requestList.length === 0 ? (
                        <div>
                            <Typography>No request with status found.</Typography>
                            <Button onClick={handleCreateRequest}>Crete new Request</Button>
                        </div>
                    ) : (
                        <List>
                            {requestList.map((req) => (
                                <ListItem button key={req.id}>
                                    <ListItemText
                                        primary={`Request ID: ${req.id}`}
                                        secondary={`Private: ${req.private ? "Yes" : "No"} | School: ${req.school}`}
                                    />
                                    <ListItemSecondaryAction>
                                        <IconButton
                                            edge="end"
                                            onClick={() => handleViewRequestDetail(req.id)}
                                        >
                                            <VisibilityIcon />
                                        </IconButton>
                                    </ListItemSecondaryAction>
                                </ListItem>
                            ))}
                        </List>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenRequestDialog(false)}>Cancel</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={openDetailDialog} onClose={() => setOpenDetailDialog(false)} maxWidth="md" fullWidth>
                <DialogTitle>Cloth Details</DialogTitle>
                <DialogContent dividers>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell>Category</TableCell>
                                <TableCell>Type</TableCell>
                                <TableCell>Color</TableCell>
                                <TableCell>Logo Pos</TableCell>
                                <TableCell>Logo Img</TableCell>
                                <TableCell>Logo Width</TableCell>
                                <TableCell>Logo Height</TableCell>
                                <TableCell>Note</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {clothList.map((cloth, idx) => (
                                <TableRow key={idx}>
                                    <TableCell>{cloth.cloth_category}</TableCell>
                                    <TableCell>{cloth.cloth_type}</TableCell>
                                    <TableCell>{cloth.color}</TableCell>
                                    <TableCell>{cloth.logo_position}</TableCell>
                                    <TableCell>
                                        {cloth.logo_image ? (
                                            <img
                                                src={cloth.logo_image}
                                                alt="Logo"
                                                style={{ width: '50px', height: 'auto' }}
                                            />
                                        ) : (
                                            <span style={{ display: 'none' }}></span>
                                        )}
                                    </TableCell>
                                    <TableCell>{cloth.logo_width}</TableCell>
                                    <TableCell>{cloth.logo_height}</TableCell>
                                    <TableCell>{cloth.note}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDetailDialog(false)}>Close</Button>

                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleChooseRequest}
                    >
                        Choose
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}
