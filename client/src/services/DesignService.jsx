import axiosClient from "../config/APIConfig.jsx";

export const createDesignRequest = async (request) => {
    const response = await axiosClient.post("/design/", request);
    return response?.data || null;
};

export const viewListHistory = async () => {
    const response = await axiosClient.get("/design/list-request");
    return response?.data || null;
}

export const getClothByRequestId = async (requestId) => {
    const res = await axiosClient.post(`/design/cloth-list`, { requestId });
    return res?.data?.data || [];
};

export const chooseDesignPackage = async ({ designRequestId, packageId }) => {
    const res = await axiosClient.post("/design/package", {
        designRequestId,
        packageId
    });
    return res?.data?.data || [];
}

export const getSampleImages = async () => {
    const response = await axiosClient.get("/design/sampleImage-list");
    return response?.data || null;
}

export const getAllComments = async (requestId) => {
    const response = await axiosClient.get(`/design/design-request/${requestId}/comments`);
    return response?.data || null;
}
