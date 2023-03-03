import { useSelector } from "react-redux";
import {StyledDiv, StyledImg, StyledText, StyledTextDiv} from "./AccountSettingsPage.style";
import avatar from "../assets/avatar.png";
const AccountSettingsPage = () => {
    const firstName = useSelector((state) => state.user.firstName);
    const lastName = useSelector((state) => state.user.lastName);
    const email = useSelector((state) => state.user.email);
    const birthday = useSelector((state) => state.user.birthdate);

    return (
        <StyledDiv>
            <StyledImg src={avatar} alt="Avatar"></StyledImg>
            <StyledTextDiv>
                <h1>{firstName} {lastName}</h1>
                <StyledText>Email</StyledText>
                <p>{email}</p>
                <StyledText>Birthday</StyledText>
                <p>{birthday}</p>
            </StyledTextDiv>
        </StyledDiv>
    );
};
export default AccountSettingsPage;