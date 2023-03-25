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
            seatPositions: String,
            ticketIds: String,
            buxOperator: String,
            startPoint: String,
            endPoint: String,
            startTime: String,
            endTime: String,
            Duration: String,
            Policy: String,
            numOfSeats: String,
            seatType: String,
            ticketCost: String,
            totalCost: String,
            status: String,
        ): MutableList<BookingDetail> {
            return mutableListOf(
                BookingDetail("Full Name", fullName, "Seat Positions", seatPositions),
                BookingDetail("Ticket Ids", ticketIds, "Bux Operator", buxOperator),
                BookingDetail("Start Point", startPoint, "End Point", endPoint),
                BookingDetail("Start Time", startTime, "End Time", endTime),
                BookingDetail("Duration", Duration, "Policy", Policy),
                BookingDetail("Number of Seats", numOfSeats, "Seat Type", seatType),
                BookingDetail("Ticket Cost", ticketCost, "Total Cost", totalCost),
                BookingDetail("Status", status, "", ""),
            )
        }
    }


}