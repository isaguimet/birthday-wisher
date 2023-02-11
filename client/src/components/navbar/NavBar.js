import Container from "react-bootstrap/esm/Container";
import Theme from "../../theme/Theme";
import { StyledNavbar, StyledLogo, NavItems, Navlink} from "./NavBar.style";
import Nav from 'react-bootstrap/Nav'
import Icons from '../../Icons';

const NavBar = () => {
    return (
        <Theme>
            
        <StyledNavbar>
            <Container>
                <StyledLogo>Brithday Wisher</StyledLogo>
                <Nav>
                    <Navlink href="#home"><img src={Icons[0]} /></Navlink>
                    <Navlink href="#friends"><img src={Icons[1]} /></Navlink>
                    <Navlink href="#notification"><img src={Icons[2]} /></Navlink>
                    <Navlink href="#profile"><img src={Icons[3]} /></Navlink>
                </Nav>
            </Container>
        </StyledNavbar>
        </Theme>


    );
};

export default NavBar;