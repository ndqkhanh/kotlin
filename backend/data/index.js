import fetch from 'node-fetch';
import { faker } from '@faker-js/faker';

export const blogList = [
  {
    id: faker.datatype.uuid(),
    thumbnail: 'https://storage.googleapis.com/vex-config/cms-tool/post/images/163/img_card.png',
    title: 'Ưu đãi các tuyến đường HOT giảm đến 50%',
    content:
      '<div style="text-align:center;"><p><strong>Thể lệ chương trình</strong></p><div><ul><li>Khách hàng áp dụng mã giảm giá,&nbsp;đặt vé và thanh toán thành công trong khung giờ<strong>&nbsp;từ 12:00 đến&nbsp;13:00 ngày 23.5.2023</strong></li><li>Chỉ áp dụng cho một số tuyến của nhà xe tham gia chương trình, có mở bán online và có&nbsp;<strong>ngày khởi hành từ 12h00 ngày 23.5 đến hết&nbsp;31.5.2023</strong>.</li><li>Mỗi mã giảm giá có giá trị giảm 50% hoặc 20% cho một lần đặt vé,&nbsp;<em><strong>giảm tối đa 250.000 vnđ&nbsp;(một lần đặt vé được đặt tối đa 01 vé)</strong></em>.</li><li>Mỗi khách hàng sử dụng mã giảm giá thuộc chương trình tối đa 01 lần.</li><li>Chương trình chỉ áp dụng khi khách hàng đặt vé tại Website/App và tổng đài đặt vé Vexere.</li><li>Nhập mã giảm giá ở bước thanh toán khi đặt vé tại Website/App Vexere. Không áp dụng hình thức thanh toán tại Nhà xe.</li><li>Vé có sử dụng mã giảm giá thuộc chương trình KHÔNG được hoàn/hủy/đổi/trả.</li><li>Số lượng mã giảm giá có hạn. Chương trình sẽ kết thúc khi mã đã được sử dụng hết.</li><li>Đối với các tuyến/chuyến chưa có giá chính thức, mã ưu đãi chỉ áp dụng trên giá vé chưa bao gồm phụ thu.</li><li>Mã giảm giá chỉ có giá trị áp dụng trong thời gian diễn ra chương trình khuyến mại, mã giảm giá không có giá trị quy đổi thành tiền mặt, hiện vật có giá trị hoặc hoàn lại tiền thừa.</li><li>Vexere có quyền thay đổi điều kiện và quy định của chương trình trong thời gian diễn ra, bạn vui lòng xem lại điều khoản sử dụng thường xuyên.</li></ul></div></div>',
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
  },
  {
    id: faker.datatype.uuid(),
    thumbnail: 'https://storage.googleapis.com/vex-config/cms-tool/post/images/161/img_hero.png',
    title: 'Dành cho KH mới - Giảm đến 25% khi đặt VeXeRe',
    content:
      '<p><strong>Vexere.com</strong> - Sàn TMĐT về vé xe lớn nhất Việt Nam, hỗ trợ người dùng đặt vé xe khách một cách dễ dàng, tiện lợi và nhanh chóng. Dùng mã ngay để nhận&nbsp;ưu đãi đến 25% cho đơn hàng đầu tiên của bạn tại VeXeRe nhé!</p><ul><li><strong>Đối tượng áp dụng:&nbsp;</strong>Áp dụng cho khách hàng&nbsp;<strong>lần đầu đặt vé tại Vexere.</strong></li><li><strong>Thời gian áp dụng:</strong>&nbsp;Áp dụng theo ngày khởi hành tuỳ theo từng nhà xe. Bạn có thể xem chi tiết thời gian áp dụng của từng nhà xe ở phần bên dưới.</li></ul>',
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
  },
  {
    id: faker.datatype.uuid(),
    thumbnail: 'https://storage.googleapis.com/vex-config/cms-tool/post/images/137/img_card.png',
    title: 'Tổng hợp chương trình khuyến mãi trong tháng',
    content: '<div><h3>Nhiều ưu đãi</h3><p>Vô số ưu đãi cực chất<br>độc quyền tại VeXeRe</p></div>',
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
  },
  {
    id: faker.datatype.uuid(),
    thumbnail: 'https://storage.googleapis.com/vex-config/cms-tool/post/images/166/img_card.png',
    title: 'Giới thiệu bạn mới - Nhận quà khủng từ VeXeRe',
    content:
      '<div><p>Đối tượng tham gia</p><ul><li style="font-size: 16px;"><p style="margin-bottom: 0"><strong>Bạn cũ (người giới thiệu):</strong> là khách hàng có từ 1 đơn hàng trở lên tại VeXeRe.</p></li><li class="color--white" style="font-size: 16px;"><p style="margin-bottom: 0"><strong>Bạn mới (người được giới thiệu):</strong> là khách hàng chưa có đơn hàng thành công tại VeXeRe.</p></li></ul></div>',
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
  },
  {
    id: faker.datatype.uuid(),
    thumbnail: 'https://storage.googleapis.com/vex-config/cms-tool/post/images/152/img_card.png',
    title: 'Ưu đãi bất ngờ khi đặt VeXeRe',
    content:
      '<h2><strong>Săn ưu đãi khủng với tính năng "Ưu đãi đặt sớm"</strong></h2><p>Không chỉ được thoải mái lựa chọn chỗ ngồi yêu thích, ngày nay, đặt vé sớm tại Vexere còn có vô vàn ưu đãi cực hấp dẫn. Nhanh tay nắm lấy cơ hội sở hữu vé xe giá rẻ chỉ có tại Vexere nhé!</p>',
    create_time: '2022-03-22T12:26:44.480Z',
    update_time: '2022-08-27T14:43:55.772Z',
  },
];
export const busImage =
  'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkbqHUzNXU8zjNSUvKcrLGvsHQHAJGSn1pGw&usqp=CAU';
export const busOperatorImage =
  'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRx3I-IWgE2fJn8t7aPObYaPgFUjchsK8S84g&usqp=CAU';

export const pointList = [
  'Bến xe buýt Chợ Bến Thành',
  'Bến xe buýt Đại học Bách Khoa',
  'Bến xe buýt Công viên 23/9',
  'Bến xe buýt Miền Đông',
  'Bến xe buýt Miền Tây',
  'Bến xe buýt Vũng Tàu',
  'Bến xe buýt Long Hải',
  'Bến xe buýt Chợ Cái Mép',
  'Bến xe buýt Phú Mỹ',
  'Bến xe buýt Phan Thiết',
  'Bến xe buýt Mũi Né',
  'Bến xe buýt Tuy Phong',
  'Bến xe buýt Đức Linh',
  'Bến xe buýt Bắc Bình',
  'Bến xe buýt Đà Lạt',
  'Bến xe buýt Bảo Lộc',
  'Bến xe buýt Đơn Dương',
  'Bến xe buýt Đức Trọng',
  'Bến xe buýt Lạc Dương',
  'Bến xe buýt Pleiku',
  'Bến xe buýt An Khê',
  'Bến xe buýt Ayun Pa',
  'Bến xe buýt Chư Prông',
  'Bến xe buýt Chư Sê',
  'Bến xe buýt Nha Trang',
  'Bến xe buýt Cam Ranh',
  'Bến xe buýt Vĩnh Lương',
  'Bến xe buýt Chợ Đầm',
  'Bến xe buýt Vạn Giã',
  'Bến xe buýt Phan Rang-Tháp Chàm',
  'Bến xe buýt Ninh Sơn',
  'Bến xe buýt Ninh Phước',
  'Bến xe buýt Ninh Hải',
  'Bến xe buýt Bắc Ái',
  'Bến xe buýt Tuy Hòa',
  'Bến xe buýt Sông Cầu',
  'Bến xe buýt Đại Lãnh',
  'Bến xe buýt Sơn Hòa',
  'Bến xe buýt Tây Hòa',
  'Bến xe buýt Cao Lãnh',
  'Bến xe buýt Sa Đéc',
  'Bến xe buýt Hồng Ngự',
  'Bến xe buýt Chợ Vàm Cống',
  'Bến xe buýt Tam Nông',
  'Bến xe buýt Rạch Giá',
  'Bến xe buýt Hà Tiên',
];
export const streetList = [
  'Cách Mạng Tháng Tám',
  'Điện Biên Phủ',
  'Lê Hồng Phong',
  'Nguyễn An Ninh',
  '30/4',
  'Nguyễn Thông',
  'Tôn Đức Thắng',
  'Lê Lai',
  'Nguyễn Chí Thanh',
  'Lê Đại Hành',
  'Phan Bội Châu',
  'Trương Công Định',
  'Phan Đình Phùng',
  'Nguyễn Trãi',
  'Lê Thánh Tôn',
  'Lý Thái Tổ',
  'Trần Nhật Duật',
  'Trần Phú',
  'Lê Thánh Tông',
  'Lý Tự Trọng',
  'Ngô Gia Tự',
  'Nguyễn Thị Minh Khai',
  'Thống Nhất',
  'Nguyễn Văn Cừ',
  'Yên Ninh',
  'Nguyễn Đình Chiểu',
  'Lý Thường Kiệt',
  'Lê Duẩn',
  'Nguyễn Huệ',
  'Trần Hưng Đạo',
  'Lê Lợi',
];

const districtUrl = 'https://provinces.open-api.vn/api/d/';
export const districtList = async () => {
  const result = await fetch(districtUrl);
  if (!result) console.log('Error');
  else {
    const data = await result.json();
    return data;
  }

  return null;
};

const wardUrl = 'https://provinces.open-api.vn/api/w/';
export const wardList = async () => {
  const result = await fetch(wardUrl);
  if (!result) console.log('Error');
  else {
    const data = await result.json();
    return data;
  }

  return null;
};

export const ticketDateList = [
  new Date('2023-05-11T09:38:41.061Z'), // qua khu,
  new Date('2023-05-12T09:38:41.061Z'), // qua khu,
  new Date('2023-05-13T09:38:41.061Z'), // qua khu,
  new Date('2023-05-14T09:38:41.061Z'), // qua khu,
  new Date('2023-05-15T09:38:41.061Z'), // qua khu,
  // new Date('2023-05-18T09:38:41.061Z'), // tuong lai
];

export const busDateList = [
  new Date('2023-05-11T09:38:41.061Z'), // qua khu,
  new Date('2023-05-12T09:38:41.061Z'), // qua khu,
  new Date('2023-05-13T09:38:41.061Z'), // qua khu,
  new Date('2023-05-14T09:38:41.061Z'), // qua khu,
  new Date('2023-05-15T09:38:41.061Z'), // qua khu,
  new Date('2023-05-16T09:38:41.061Z'), // hien tai,
  new Date('2023-05-17T09:38:41.061Z'), // tuong lai
  new Date('2023-05-18T09:38:41.061Z'), // tuong lai
  new Date('2023-05-19T09:38:41.061Z'), // tuong lai
  new Date('2023-05-20T09:38:41.061Z'), // tuong lai
  new Date('2023-05-21T09:38:41.061Z'), // tuong lai
];
