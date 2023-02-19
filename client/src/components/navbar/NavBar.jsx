import Container from "react-bootstrap/esm/Container";
import Theme from "../../theme/Theme";
import { StyledNavbar, StyledLogo, NavItems, Navlink, StyledImg } from "./NavBar.style";
import Nav from 'react-bootstrap/Nav'
import Icons from '../../Icons';

const NavBar = () => {
    return (
        <Theme>

            <StyledNavbar>
                <Container>
                    <StyledLogo>Birthday Wisher</StyledLogo>
                    <Nav>
                        <Navlink href="#home"><StyledImg src={Icons[0]} /></Navlink>
                        <Navlink href="#friends"><StyledImg src={Icons[1]} /></Navlink>
                        <Navlink href="#notification"><StyledImg src={Icons[2]} /></Navlink>
                        <Navlink href="#profile"><StyledImg src={Icons[3]} /></Navlink>
                    </Nav>
                </Container>
            </StyledNavbar>
        </Theme>


    );
};

export default NavBar;