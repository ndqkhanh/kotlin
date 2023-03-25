package com.example.kotlin

data class BookingDetail(
    var title1: String?,
    var content1: String?,
    var title2: String?,
    var content2: String?
)

class BookingDetailList {
    companion object {
        var bookingDetails = mutableListOf<BookingDetail>()

        fun generateBookingDetailList(
            fullName: String,
            email: String,
            seatPositions: String,
            ticketIds: String,
            buxOperator: String,
            startPoint: String,
            endPoint: String,
            startTime: String,
            endTime: String,
            Duration: String,
            numOfSeats: String,
            seatType: String,
            ticketCost: String,
            totalCost: String,
            status: String,
        ): MutableList<BookingDetail> {
            return mutableListOf(
                BookingDetail("Full Name", fullName, "Email", email),
                BookingDetail("Seat Positions", seatPositions, "Ticket IDs", ticketIds),
                BookingDetail("Bux Operator", buxOperator, "Start Point", startPoint),
                BookingDetail("End Point", endPoint, "Start Time", startTime),
                BookingDetail("End Time", endTime, "Duration", Duration),
                BookingDetail("Number of Seats", numOfSeats, "Seat Type", seatType),
                BookingDetail("Ticket Cost", ticketCost, "Total Cost", totalCost),
                BookingDetail("Status", status, "", "")
            )
        }
    }


}