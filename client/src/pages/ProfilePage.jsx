import avatar from "../assets/avatar.png";
import "./ProfilePage.css";

const ProfilePage = () => {

    return (
        <div className="container">
            <img src={avatar} alt="Avatar"></img>
            <div className="right-container">
                <h1>Bobby Bob</h1>
                <p className="title">Email</p>
                <p>Bobby@gmail.com</p>
                <p className="title">Birthday</p>
                <p>January 1, 2000</p>
                <button className="button">Edit</button>
            </div>
        </div>
    );
};

export default ProfilePage;