package DatabaseGrapher;

import DatabaseGrapher.Driver.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Driver driver = new Driver();
        driver.run();
    }
}
