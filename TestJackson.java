import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJackson {
    public static void main(String[] args) throws Exception {
        TestDTO dto = new TestDTO();
        dto.setFVencimiento("2023-10-15");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(dto));
    }
}
class TestDTO {
    private String fVencimiento;
    public String getFVencimiento() { return fVencimiento; }
    public void setFVencimiento(String f) { this.fVencimiento = f; }
}
