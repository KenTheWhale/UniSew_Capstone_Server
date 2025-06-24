import React, {useEffect, useState} from 'react';
import {
    Box,
    Tabs,
    Tab,
    Typography,
    TextField,
    Button,
    List,
    ListItem,
    Paper,
    Divider,
    Stack
} from '@mui/material';
import {useLocation} from "react-router-dom";
import {getAllComments} from "../../services/DesignService.jsx";

function TabPanel(props) {
    const { children, value, index, ...other } = props;
    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            {...other}
        >
            {value === index && (
                <Box p={2}>{children}</Box>
            )}
        </div>
    );
}


const ActivityTab = ({
                         commentInput,
                         setCommentInput,
                         handleSendComment,
                     }) => {
    const [listComment, setListComment] = useState([]);
    const location = useLocation();
    const requestId = location.state?.requestId;
    const packageId = location.state?.packageId;

    const user = JSON.parse(localStorage.getItem("user") || "{}");

    useEffect(() => {
        if (!requestId) return;
        const fetchComments = async () => {
            try {
                const data = await getAllComments(requestId);
                setListComment(Array.isArray(data) ? data : []);
            } catch (error) {
                console.error('Error loading comments:', error);
                setListComment([]);
            }
        };
        fetchComments();
    }, [requestId]);

    // Nếu chưa assign designer
    if (packageId == null || packageId === 0) {
        return (
            <Box mt={2}>
                <Typography color="gray" textAlign="center" mb={2}>
                    This request has not been assigned yet.
                </Typography>
                <Stack direction="column" spacing={2} alignItems="center">
                    <Button variant="contained" color="primary">
                        Find the designer for this request
                    </Button>
                    <Button variant="outlined" color="secondary">
                        Find garment to do this request
                    </Button>
                </Stack>
            </Box>
        );
    }

    return (
        <Box>
            <List>
                {(Array.isArray(listComment) ? listComment : []).length === 0 ? (
                    <Typography color="gray" textAlign="center" mb={2}>
                        Waiting for designer response...
                    </Typography>
                ) : (
                    (listComment || []).map((c, i) => (
                        <ListItem key={i} alignItems="flex-start">
                            <Box ml={2}>
                                <Typography fontWeight="bold">{c.user || c.senderRole}</Typography>
                                <Typography>{c.text || c.content}</Typography>
                                <Typography variant="caption" color="gray">{c.time || c.createdAt}</Typography>
                            </Box>
                        </ListItem>
                    ))
                )}
            </List>

            {!(user.role === "school" && listComment.length === 0) && (
                <Box display="flex" gap={1} mt={2}>
                    <TextField
                        fullWidth
                        placeholder="Write a comment..."
                        size="small"
                        value={commentInput}
                        onChange={e => setCommentInput(e.target.value)}
                        onKeyDown={e => e.key === 'Enter' && handleSendComment()}
                    />
                    <Button
                        variant="contained"
                        onClick={handleSendComment}
                        disabled={!commentInput.trim()}
                    >
                        Send
                    </Button>
                </Box>
            )}

            {user.role === "school" && listComment.length === 0 && (
                <Typography color="gray" mt={2}>
                    Please wait for the designer to start the conversation.
                </Typography>
            )}
        </Box>
    );
};

const DetailsTab = ({ details }) => (
    <Box>
        <Typography variant="h6" mb={1}>{details.packageName}</Typography>
        <Typography>Price: <b>{details.price}</b></Typography>
        <Typography>Delivery Time: <b>{details.deliveryTime}</b></Typography>
        <Typography mt={1} fontWeight="bold">Includes:</Typography>
        <ul>
            {details.includes.map((inc, i) => (
                <li key={i}><Typography>{inc}</Typography></li>
            ))}
        </ul>
    </Box>
);

const RequirementsTab = ({ requirements }) => (
    <Box>
        <Typography variant="h6" mb={1}>Your Requirements</Typography>
        <List>
            {requirements.map((r, i) => (
                <ListItem key={i}>
                    <Box>
                        <Typography fontWeight="bold">{r.label}</Typography>
                        <Typography>Type: {r.type}</Typography>
                        <Typography>Color: {r.color}</Typography>
                        <Typography>Note: {r.note}</Typography>
                    </Box>
                </ListItem>
            ))}
        </List>
    </Box>
);

const DeliveryTab = ({ delivery }) => (
    <Box>
        <Typography variant="h6" mb={1}>Delivery #1</Typography>
        <Box bgcolor="#f7f7f7" p={2} borderRadius={2} mb={2}>
            <Typography variant="body2" dangerouslySetInnerHTML={{__html: delivery.message}} />
        </Box>
        <Typography fontWeight="bold">Attachments:</Typography>
        <Box display="flex" gap={2} mt={1}>
            {delivery.files.map((file, i) => (
                <Box key={i} textAlign="center">
                    <img src={file.preview} alt={file.name} width={70} style={{borderRadius:8, border:'1px solid #eee'}} />
                    <Typography fontSize={12}>{file.name}</Typography>
                    <a href={file.url} download>
                        <Button size="small" variant="outlined" sx={{mt: 0.5}}>Download</Button>
                    </a>
                </Box>
            ))}
        </Box>
    </Box>
);

export default function RequestDetail({ details, requirements, delivery, initComments }) {
    const [tab, setTab] = useState(0);

    const [comments, setComments] = useState(initComments || []);
    const [commentInput, setCommentInput] = useState('');

    // Handler for sending comment
    const handleSendComment = () => {
        if (commentInput.trim()) {
            setComments([...comments, { user: "You", text: commentInput, time: "just now" }]);
            setCommentInput('');
        }
    };

    return (
        <Paper elevation={2} sx={{ width: "100%", maxWidth: 700, mx: "auto", mt: 3 }}>
            <Tabs value={tab} onChange={(_, v) => setTab(v)} variant="fullWidth">
                <Tab label="Activity" />
                <Tab label="Details" />
                <Tab label="Requirements" />
                <Tab label="Delivery" />
            </Tabs>
            <Divider />
            <TabPanel value={tab} index={0}>
                <ActivityTab
                    comments={comments}
                    commentInput={commentInput}
                    setCommentInput={setCommentInput}
                    handleSendComment={handleSendComment}
                />
            </TabPanel>
            <TabPanel value={tab} index={1}>
                <DetailsTab details={details} />
            </TabPanel>
            <TabPanel value={tab} index={2}>
                <RequirementsTab requirements={requirements} />
            </TabPanel>
            <TabPanel value={tab} index={3}>
                <DeliveryTab delivery={delivery} />
            </TabPanel>
        </Paper>
    );
}
