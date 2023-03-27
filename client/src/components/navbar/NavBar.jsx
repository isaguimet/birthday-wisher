import Container from "react-bootstrap/esm/Container";
import Theme from "../../theme/Theme";
import {Navlink, StyledButton, StyledImg, StyledLogo, StyledNavbar} from "./NavBar.style";
import Nav from 'react-bootstrap/Nav'
import Icons from '../../Icons';
import {useDispatch, useSelector} from "react-redux";
import {setLogout} from "../../store/user";

const NavBar = () => {
    const dispatch = useDispatch();
    const user = useSelector((state) => state.user.id);
    return (
        <Theme>
            <StyledNavbar>
                <Container>
                    <StyledLogo href="/">Birthday Wisher</StyledLogo>
                    {user && (
                        <Nav>
                            <Navlink href="/wishing-center"><StyledImg src={Icons[0]}/></Navlink>
                            <Navlink href="/friends"><StyledImg src={Icons[1]}/></Navlink>
                            <Navlink href={`/profile/${user}`}><StyledImg src={Icons[2]}/></Navlink>
                            <Navlink href={"/"}><StyledButton onClick={() => dispatch(setLogout())}>Logout</StyledButton></Navlink>
                        </Nav>
                    )}
                </Container>
            </StyledNavbar>
        </Theme>
    );
};

export default NavBar;
