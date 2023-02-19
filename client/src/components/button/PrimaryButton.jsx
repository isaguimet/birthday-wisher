export default function PrimaryButton({
    height = "2.5rem",
    width = "4.5rem",
    backgroundColor = "#ED7966",
    color = "white",
    border = "none",
    fontSize = "18px",
    buttonText, }) {
    return (
        <button style={{ width, height, backgroundColor, color, border, fontSize, borderRadius: "10px" }}>
            {buttonText}
        </button>
    )
}