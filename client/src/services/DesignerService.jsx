import axiosClient from "../config/APIConfig.jsx";

export const viewListDesigner = async () => {
    const response = await axiosClient.get("/profile/designer/list");
    return response?.data || null;
}