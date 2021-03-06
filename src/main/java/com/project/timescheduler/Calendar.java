package com.project.timescheduler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Objects;

public class Calendar {

    private final GridPane calendarGridPane;
    private final Label currentYearLabel;
    private final OnMouseClickedListener listener;
    private final LocalDate todaysDate;
    private LocalDate currentDate;

    private enum WEEK_DAY{
        MONDAY      (0),
        TUESDAY     (1),
        WEDNESDAY   (2),
        THURSDAY    (3),
        FRIDAY      (4),
        SATURDAY    (5),
        SUNDAY      (6);

        private final int code;

        WEEK_DAY(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    interface OnMouseClickedListener{
        void onMouseClicked(MouseEvent mouseEvent);
    }

    public Calendar(ArrayList<Node> nodeArrayList, OnMouseClickedListener listener, LocalDate currentDate){
        this.calendarGridPane = (GridPane) nodeArrayList.get(0);
        this.currentYearLabel = (Label) nodeArrayList.get(1);

        this.currentDate = currentDate;
        this.todaysDate = currentDate;
        this.listener = listener;
    }

    public void initializeCalendar() throws IOException, InterruptedException {
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        for (int row = 0; row < rowCount; row++){
            for (int column = 0; column < columnCount; column++) {
                VBox vBox = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("calendarItem.fxml")));
                Label label = (Label) vBox.getChildren().get(0);

                if (column == 0 && row > 0){
                    label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
                    calendarGridPane.add(vBox, column, row);

                }else if (column > 0 && row > 0){
                    vBox.setDisable(startDate.getMonth() != currentDate.getMonth());

                    label.setText(String.format("%d", startDate.getDayOfMonth()));

                    if (todaysDate.equals(startDate)){
                        label.setTextFill(Color.RED);
                    }

                    vBox.setOnMouseClicked(listener::onMouseClicked);
                    calendarGridPane.add(vBox, column, row);
                    startDate = startDate.plusDays(1);
                }

            }
        }
    }

    public void updateCalendar(){
        int columnCount = calendarGridPane.getColumnCount() - 1;    // - 1, weil man von 0 anfängt
        int rowCount = calendarGridPane.getRowCount() - 1;

        int calenderLength = (columnCount * rowCount) - 7 - 1;

        LocalDate startDate = currentDate.withDayOfMonth(1);
        startDate = startDate.minusDays(WEEK_DAY.valueOf(startDate.getDayOfWeek().toString()).getCode());

        System.out.println("Start Date: " + startDate);
        System.out.println("Current Date: " + currentDate);

        for (int i = 0; i < calenderLength; i++) {
            VBox vBox = (VBox) calendarGridPane.getChildren().get(i + 7 + 1); // +7 wegen der ersten Zeile und +1 wegen Group Element
            Label label = (Label) vBox.getChildren().get(0);

            if (i % 8 == 0){
                label.setText(String.valueOf(startDate.get(WeekFields.ISO.weekOfYear()))); //KW
            }else {
                vBox.setDisable(startDate.getMonth() != currentDate.getMonth());
                label.setText(String.format("%d", startDate.getDayOfMonth()));

                if (todaysDate.equals(startDate)){
                    label.setTextFill(Color.RED);
                }else {
                    label.setTextFill(Color.BLACK);
                }

                startDate = startDate.plusDays(1);
            }
        }

    }

    public void nextMonth() throws IOException {
        currentDate = currentDate.plusMonths(1);
        currentYearLabel.setText(currentDate.toString());
        updateCalendar();
    }

    public void previousMonth() throws IOException {
        currentDate = currentDate.minusMonths(1);
        currentYearLabel.setText(currentDate.toString());
        updateCalendar();
    }

}
