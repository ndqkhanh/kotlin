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
            seatPositions: List<Int>,
            ticketIds: List<String>,
            phone: String,
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
            var seatPositionsString = ""
            for (i in seatPositions.indices) {
                seatPositionsString += "<li>" + seatPositions[i] + "</li>"
            }
            seatPositionsString = "<ul>$seatPositionsString</ul>"

            var ticketIdsString = ""
            for (i in ticketIds.indices) {
                ticketIdsString += "<li>" + ticketIds[i] + "</li>"
            }
            ticketIdsString = "<ul>$ticketIdsString</ul>"

            return mutableListOf(
                BookingDetail("Full Name", fullName, "Email", email),
                BookingDetail("Seat Positions", seatPositionsString, "Ticket IDs", ticketIdsString),
                BookingDetail("Phone", phone, "Bus Operator", buxOperator),
                BookingDetail("Start Point", startPoint, "End Point", endPoint),
                BookingDetail("Start Time", startTime, "End Time", endTime),
                BookingDetail("Duration", Duration, "Number of Seats", numOfSeats),
                BookingDetail("Seat Type", seatType, "Ticket Cost", ticketCost),
                BookingDetail("Total Cost", totalCost, "Status", status),
            )
        }
    }


}