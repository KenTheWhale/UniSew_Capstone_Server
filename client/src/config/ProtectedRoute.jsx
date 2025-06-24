
export default function ProtectedRoute({ children, allowedRoles }) {
    console.log("This page for ",allowedRoles)
    return (
        {children}
    )
}