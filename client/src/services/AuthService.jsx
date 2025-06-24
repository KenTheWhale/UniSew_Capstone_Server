import axiosClient from "../config/APIConfig.jsx";


export const getGoogleUrl = async () => {
    const response = await axiosClient.get("/auth/google/url")
    return response ? response : null
}

export const login = async (email, avatar, name) => {
    const response = await axiosClient.post("/auth/login", {
        email: email,
        avatar: avatar,
        name: name
    })
    return response ? response : null
}