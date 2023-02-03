import {BrowserRouter, Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import PersonalBoardPage from "./pages/PersonalBoardPage";
import ProfilePage from "./pages/ProfilePage";
import WishingCenterPage from "./pages/WishingCenterPage";
import NavBar from "./components/NavBar";

function App() {
    return (
        <div className="app">
            <BrowserRouter>
                <NavBar/>
                <Routes>
                    <Route path={"/"} element={<LoginPage/>}/>
                    <Route path={"/home"} element={<HomePage/>}/>
                    <Route path={"/personal-board"} element={<PersonalBoardPage/>}/>
                    <Route path={"/profile"} element={<ProfilePage/>}/>
                    <Route path={"/wishing-center"} element={<WishingCenterPage/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
