import avatar from "../assets/avatar.png";
import "./ProfilePage.css";

const ProfilePage = () => {

    return (
        <div className="container">
            <img className="profile-picture" src={avatar} alt="Avatar"></img>
            <div className="right-container">
                <h1>Bobby Bob</h1>
                <p className="title">Email</p>
                <p>Bobby@gmail.com</p>
                <p className="title">Birthday</p>
                <p>January 1, 2000</p>
            </div>
        </div>
    );
};

export default ProfilePage;