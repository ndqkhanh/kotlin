const allRoles = {
  user: ['createTicket', 'updateAvatar', 'seeHistory', 'createReview', 'printTicket', 'discardTicket', 'payTicket'],

  bus_operator: [
    'bookingList',
    'searchBooking',
    'busList',
    'cloneBus',
    'deleteTicket',
    'updateTicket',
    'bookingGet',
    'bookingUpdate',
    'createBus',
    'deleteBus',
    'updateBus',
    'getBus',
    'deleteBlog',
    'createBlog',
    'bookingDelete',
    'busOperatorList',
  ],

  admin: ['getBOByID', 'viewBO', 'createBO', 'updateBO', 'deteleBO'],
};

allRoles.bus_operator = [...allRoles.bus_operator, ...allRoles.user];
allRoles.admin = [...allRoles.admin, ...allRoles.bus_operator];

console.log('admin ', allRoles.admin);
const roles = Object.keys(allRoles);
const roleRights = new Map(Object.entries(allRoles));

module.exports = {
  roles,
  roleRights,
};
