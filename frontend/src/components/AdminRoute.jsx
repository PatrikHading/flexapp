import { Navigate } from "react-router-dom";

function AdminRoute({ user, children }) {
    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (user.role !== "ADMIN") {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
}

export default AdminRoute;