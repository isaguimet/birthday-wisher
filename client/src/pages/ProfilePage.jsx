import avatar from "../assets/avatar.png";
import "./ProfilePage.css";
import PrimaryButton from "../components/button/PrimaryButton";

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
                <PrimaryButton buttonText="Edit"></PrimaryButton>
            </div>
        </div>
    );
};

export default ProfilePage;