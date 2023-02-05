import {Navigate, Outlet} from "react-router-dom";
import {useSelector} from "react-redux";

const PrivateRoute = () => {
    const isLoggedIn = Boolean(useSelector((state) => state.user));

    return isLoggedIn ? <Outlet/> : <Navigate to={"/"} replace/>;
};

export default PrivateRoute;