/* eslint-disable prettier/prettier */

const convertDateToString = (date) => {
  const time = date.toLocaleTimeString('en-US', { hour12: false, hour: 'numeric', minute: 'numeric' });
  const dayOfWeek = date.getUTCDay();
  const month = date.getUTCMonth() + 1; // Add 1 to the month to make it 1-indexed instead of 0-indexed
  const dayOfMonth = date.getUTCDate();
  const year = date.getUTCFullYear();
  let weekOfMonth;
  if (dayOfWeek === 0) {
    weekOfMonth = 'CN';
  } else {
    // Otherwise, calculate the week of the month based on the day of the month and day of the week
    // const firstDayOfMonth = new Date(Date.UTC(year, month - 1, 1));
    // const daysSinceFirstMonday = ((7 - (firstDayOfMonth.getUTCDay() - 1)) % 7) + (dayOfWeek - 1);
    weekOfMonth = `T${dayOfWeek + 1}`;
  }
  const dateStr = `${weekOfMonth},${dayOfMonth.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year}`;

  const formattedDate = `${time} • ${dateStr}`;
  return formattedDate;
};

module.exports = { convertDateToString };
