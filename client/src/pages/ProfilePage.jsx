import Theme from "../theme/Theme";
import {StyledDiv} from "./ProfilePage.style"
import {useLocation} from "react-router-dom";
import BoardSection from "../components/boardSection/BoardSection";
import ProfileSection from "../components/profileSection/ProfileSection";

/**
 * A component for displaying a user's profile. This includes:
 *  - Profile Section: displays personal info about a user.
 *  - Board Section: displays the birthday boards of a user.
 * Both of the above sections make their own API calls and therefore each maintain their own state of data and errors.
 * Both sections also conditionally render certain components based on whether the person viewing the profile is the
 * owner of the profile.
 * @param props {object} No expected props for this component.
 * @returns {JSX.Element}
 * @constructor
 */
const ProfilePage = (props) => {
    // fetch the ID of the user whose profile is shown on this page (from the URL)
    const path = useLocation().pathname;
    const profileUser = path.substring(path.lastIndexOf("/") + 1);

    return (
        <Theme>
            <StyledDiv>
                <ProfileSection profileUser={profileUser}/>
                <BoardSection profileUser={profileUser}/>
            </StyledDiv>
        </Theme>
    );
};

export default ProfilePage;