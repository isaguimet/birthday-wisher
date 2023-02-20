import Card from 'react-bootstrap/Card';


const BirthdayCard = (props) => {

    return (
        <Card style={{ width: '18rem' }}>
        <Card.Body>
            <Card.Text>
            {props.children}
            </Card.Text>
        </Card.Body>
        </Card>
    );

};

export default BirthdayCard