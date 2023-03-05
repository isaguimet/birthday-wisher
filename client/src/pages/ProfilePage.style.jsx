import styled from 'styled-components';

export const StyledDiv = styled.div`
    padding-right: 15rem;
    padding-left: 15rem;
`;

export const Profile = styled.div`
    display: flex;
    justify-content: center;
    padding-top: 2rem;
`;

export const Board = styled.div`
    display: flex;
    justify-content: left;
    padding-top: 1rem;
    padding-bottom: 1rem;
`;

export const BoardContainer = styled.div`
    padding: 2rem 0;
`;

export const ProfileInfo = styled.div`
    display: inline;
`;

export const Age = styled.div`
    display: inline;
    background-color: ${props => props.theme.color.peach};  
    border-radius: 10%;
`;
