import { ProfilePicture, ProfileContainer } from "./ProfilePic.style";

const ProfilePic = (props) => {
    
    return (
        <ProfileContainer>
            <ProfilePicture src={props.src} />
        </ProfileContainer>
    
    );


};

export default ProfilePic;