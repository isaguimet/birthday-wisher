import {BrowserRouter, Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import ProfilePage from "./pages/ProfilePage";
import WishingCenterPage from "./pages/WishingCenterPage";
import FriendsPage from "./pages/FriendsPage";
import NavBar from "./components/navbar/NavBar";
import PrivateRoute from "./components/PrivateRoute";
import Theme from "./theme/Theme";

function App() {
    return (
        <Theme>
        <div className="app">
            <BrowserRouter>
                <NavBar/>
                <Routes>
                    <Route path={"/"} element={<LoginPage/>}/>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/home"} element={<HomePage/>}/>
                    </Route>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/friends"} element={<FriendsPage/>}/>
                    </Route>

                    {/* <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/personal-board"} element={<PersonalBoardPage/>}/>
                    </Route> */}

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/profile/:userId"} element={<ProfilePage/>}/>
                    </Route>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/wishing-center"} element={<WishingCenterPage/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
        </Theme>
    );
}

export default App;
