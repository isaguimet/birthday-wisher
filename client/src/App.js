import {BrowserRouter, Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import PersonalBoardPage from "./pages/PersonalBoardPage";
import ProfilePage from "./pages/ProfilePage";
import WishingCenterPage from "./pages/WishingCenterPage";
import NavBar from "./components/NavBar";
import PrivateRoute from "./components/PrivateRoute";

function App() {
    return (
        <div className="app">
            <BrowserRouter>
                <NavBar/>
                <Routes>
                    <Route path={"/"} element={<LoginPage/>}/>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/home"} element={<HomePage/>}/>
                    </Route>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/personal-board"} element={<PersonalBoardPage/>}/>
                    </Route>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/profile"} element={<ProfilePage/>}/>
                    </Route>

                    <Route path={"/"} element={<PrivateRoute/>}>
                        <Route path={"/wishing-center"} element={<WishingCenterPage/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
