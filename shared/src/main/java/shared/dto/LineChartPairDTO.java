package shared.dto;

import java.time.LocalDate;

public class LineChartPairDTO {

    //date as epoch days
    long x = 0;
    double y = 0;

    public  LineChartPairDTO(){};

    public LineChartPairDTO(long x, double y) {
        this.x = x;
        this.y = y;
    }

    public LineChartPairDTO(LocalDate x, double y) {
        this.x = x.toEpochDay();
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public LocalDate getXAsLocalDate() {return LocalDate.ofEpochDay(x);};

    public void setX(LocalDate x) {
        this.x = x.toEpochDay();
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(long x) {
        this.x = x;
    }
}
