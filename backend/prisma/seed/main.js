/* eslint-disable import/extensions */
/* eslint-disable import/prefer-default-export */
/* eslint-disable camelcase */
/* eslint-disable no-console */

import { PrismaClient } from '@prisma/client';
import { faker } from '@faker-js/faker';
import {
  pointList,
  wardList,
  districtList,
  streetList,
  blogList,
  busOperatorImage,
  busImage,
  dateList,
} from '../../data/index.js';

const prisma = new PrismaClient();
faker.locale = 'vi';

const BUS_OPERATORS = [];
const BUS_STATIONS = [];
const BUSES = [];
const BUS_TICKETS = [];
const REVIEWS = [];
const POINTS = [];
const POINT_BS = [];
const USERS = [
  {
    id: 'c118f693-8722-4461-a79d-d76991b96a9e',
    email: 'nguyenphucbao68@gmail.com',
    role: 0,
    password: Buffer.from('$2a$10$uR5S.P86tXoBfCHl0a03bePKyN/1yE/1oCW5oRNs/IYfbDeL.WY9O'),
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
    display_name: 'Nguyễn Phúc Bảo',
    avatar_url:
      'https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png',
  },
  {
    id: 'c118f693-8722-4461-a79d-d76991b96bcd',
    email: 'khanhquang@gmail.com',
    role: 0,
    password: Buffer.from('$2a$10$uR5S.P86tXoBfCHl0a03bePKyN/1yE/1oCW5oRNs/IYfbDeL.WY9O'),
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
    display_name: 'Nguyễn Đinh Quang Khánh',
    avatar_url:
      'https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png',
  },
  {
    id: 'c118f693-8722-4461-a79d-d76991b96afd',
    email: 'busop1@gmail.com',
    role: 1,
    password: Buffer.from('$2a$10$uR5S.P86tXoBfCHl0a03bePKyN/1yE/1oCW5oRNs/IYfbDeL.WY9O'),
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
    display_name: 'Nguyễn Đinh Quang Nhân',
    avatar_url:
      'https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png',
  },
  {
    id: 'c118f693-8722-4461-a79d-d76991b96acf',
    email: 'busop2@gmail.com',
    role: 1,
    password: Buffer.from('$2a$10$uR5S.P86tXoBfCHl0a03bePKyN/1yE/1oCW5oRNs/IYfbDeL.WY9O'),
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
    display_name: 'Võ Duy Khánh',
    avatar_url:
      'https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png',
  },
  {
    id: 'c118f693-8722-4461-a79d-d76991b96fdf',
    email: 'user1@gmail.com',
    role: 2,
    password: Buffer.from('$2a$10$uR5S.P86tXoBfCHl0a03bePKyN/1yE/1oCW5oRNs/IYfbDeL.WY9O'),
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
    display_name: 'Nguyễn Trung Kiên',
    avatar_url:
      'https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png',
  },
  {
    id: 'c118f693-8722-4461-a79d-d76991b96abf',
    email: 'user2@gmail.com',
    role: 2,
    password: Buffer.from('$2a$10$uR5S.P86tXoBfCHl0a03bePKyN/1yE/1oCW5oRNs/IYfbDeL.WY9O'),
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
    display_name: 'Trần Thị Hương',
    avatar_url:
      'https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png',
  },
];

const createBusOperator = () => {
  return {
    id: faker.datatype.uuid(),
    image_url: busOperatorImage,
    phone: faker.phone.number('##########'),
    name: faker.name.fullName(),
  };
};

const createBusStation = () => {
  const val = faker.address.cityName();
  return {
    id: faker.datatype.uuid(),
    name: val,
    location: val,
  };
};

const createLocation = (wards, districts) => {
  return `${faker.address.buildingNumber()} ${streetList[Math.floor(Math.random() * streetList.length)]}, ${
    wards[Math.floor(Math.random() * wards.length)].name
  }, ${districts[Math.floor(Math.random() * districts.length)].name} `;
};

const createPoint = (wards, districts) => {
  return {
    id: faker.datatype.uuid(),
    name: pointList[Math.floor(Math.random() * pointList.length)],
    location: createLocation(wards, districts),
    // bs_id: BUS_STATIONS[Math.floor(Math.random() * BUS_STATIONS.length)].id,
  };
};

const createPointBs = () => {
  return {
    point_id: POINTS[Math.floor(Math.random() * POINTS.length)].id,
    bs_id: BUS_STATIONS[Math.floor(Math.random() * BUS_STATIONS.length)].id,
  };
};

// select count(*) as sl, DATE(start_time), start_point, end_point
// from buses
// group by start_point, end_point, DATE(start_time)
// order by sl desc, DATE(start_time) desc

const createBuses = () => {
  const time = new Date('2023-05-16T09:38:41.061Z');
  const month = 262974383;

  return {
    id: faker.datatype.uuid(),
    bo_id: BUS_OPERATORS[Math.floor(Math.random() * BUS_OPERATORS.length)].id,
    start_point: BUS_STATIONS[Math.floor(Math.random() * BUS_STATIONS.length)].id,
    end_point: BUS_STATIONS[Math.floor(Math.random() * BUS_STATIONS.length)].id,
    type: Math.floor(Math.random() * 3),
    start_time: time,
    end_time: faker.datatype.datetime({ min: time.getTime(), max: time.getTime() + month }),
    image_url: busImage,
    policy:
      '<ul><li>KHI TRÊN XE<ul><li>Giữ chắc vé</li><li>Giữ trật tự</li></ul></li><li>Quản lý tư trang<ul><li>Không quá 10kg</li></ul></li></ul>',
    num_of_seats: faker.datatype.number({ min: 10, max: 50 }),
    price: faker.datatype.number({ min: 10000, max: 100000 }),
  };
};

function generateString(n) {
  let numbers = [];
  for (let i = 0; i < n; i++) {
    numbers.push(i);
  }
  return numbers.join(',');
}

const createBusTickets = () => {
  const bus = BUSES[Math.floor(Math.random() * BUSES.length)];
  const num_of_seats = Math.floor(Math.random() * 5) + 1;

  const time = dateList[Math.floor(Math.random() * dateList.length)];
  const month = 262974383;
  for (let i = 0; i < num_of_seats; i++)
    return {
      id: faker.datatype.uuid(),
      bus_id: bus.id,
      user_id: USERS[Math.floor(Math.random() * USERS.length)].id,
      name: faker.name.fullName(),
      phone: faker.phone.number('##########'),
      seats: generateString(num_of_seats),
      status: faker.datatype.number({ min: 0, max: 2 }),
      pick_up_point: POINTS[Math.floor(Math.random() * POINTS.length)].id,
      drop_down_point: POINTS[Math.floor(Math.random() * POINTS.length)].id,
      note: faker.lorem.paragraph(),
      num_seats: num_of_seats,
      create_time: time,
      update_time: time,
    };
};

const createReview = () => {
  return {
    id: faker.datatype.uuid(),
    comment: faker.lorem.lines(1),
    user_id: USERS[Math.floor(Math.random() * USERS.length)].id,
    bo_id: BUS_OPERATORS[Math.floor(Math.random() * BUS_OPERATORS.length)].id,
    rate: faker.datatype.number({ min: 1, max: 5 }),
  };
};

async function main() {
  const wards = await wardList();
  const districts = await districtList();

  Array.from({ length: 15 }).forEach(() => {
    BUS_OPERATORS.push(createBusOperator());
  });

  Array.from({ length: 15 }).forEach(() => {
    BUS_STATIONS.push(createBusStation());
  });

  Array.from({ length: 20 }).forEach(() => {
    POINTS.push(createPoint(wards, districts));
  });

  BUS_STATIONS.forEach((bs) => {
    POINT_BS.push({
      point_id: POINTS[Math.floor(Math.random() * POINTS.length)].id,
      bs_id: bs.id,
    });
  });
  Array.from({ length: 10000 }).forEach(() => {
    BUSES.push(createBuses());
  });

  Array.from({ length: 400 }).forEach(() => {
    BUS_TICKETS.push(createBusTickets());
  });

  Array.from({ length: 1000 }).forEach(() => {
    REVIEWS.push(createReview());
  });
  const database = {
    users: USERS,
    bus_operators: BUS_OPERATORS,
    bus_stations: BUS_STATIONS,
    points: POINTS,
    point_bs: POINT_BS,
    buses: BUSES,
    reviews: REVIEWS,
    blogs: blogList,
    bus_tickets: BUS_TICKETS,
  };

  // eslint-disable-next-line no-restricted-syntax
  for (const [key, value] of Object.entries(database)) {
    // eslint-disable-next-line no-await-in-loop
    await prisma[key].createMany({
      data: value,
      skipDuplicates: true,
    });
    console.log(`Seeded ${key}!`);
  }
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
