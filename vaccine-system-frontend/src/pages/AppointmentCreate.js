import React, { useEffect, useState } from 'react';
import {
  Form,
  Select,
  DatePicker,
  Button,
  message,
  Spin,
  Card,
  Space,
  Input,
} from 'antd';
import {
  userApi,
  vaccineApi,
  vaccinationPointApi,
  appointmentApi,
} from '../api';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';

const { Option } = Select;
const { TextArea } = Input;

function AppointmentCreate() {
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [users, setUsers] = useState([]);
  const [vaccines, setVaccines] = useState([]);
  const [vaccinationPoints, setVaccinationPoints] = useState([]);
  const [selectedPoint, setSelectedPoint] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedVaccineId, setSelectedVaccineId] = useState(null);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      
      const [usersRes, vaccinesRes, pointsRes] = await Promise.all([
        userApi.getAll(),
        vaccineApi.getAll(),
        vaccinationPointApi.getAll(),
      ]);

      if (usersRes.success) setUsers(usersRes.data);
      if (vaccinesRes.success) setVaccines(vaccinesRes.data);
      if (pointsRes.success) setVaccinationPoints(pointsRes.data);
    } catch (error) {
      console.error('Failed to fetch initial data:', error);
      message.error('获取初始数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handlePointChange = (pointId) => {
    const point = vaccinationPoints.find(p => p.id === pointId);
    setSelectedPoint(point);
    form.setFieldsValue({ timeSlot: undefined });
  };

  const handleDateChange = (date) => {
    setSelectedDate(date);
    form.setFieldsValue({ timeSlot: undefined });
  };

  const handleVaccineChange = (vaccineId) => {
    setSelectedVaccineId(vaccineId);
    form.setFieldsValue({ doseNumber: undefined });
  };

  const getDoseOptions = () => {
    const vaccine = vaccines.find(v => v.id === selectedVaccineId);
    if (!vaccine) return [];

    const requiredDoses = vaccine.requiredDoses || 1;
    const options = [];
    for (let i = 1; i <= requiredDoses; i++) {
      options.push({
        value: i,
        label: `第${i}剂`,
      });
    }
    return options;
  };

  const getAvailableTimeSlots = () => {
    if (!selectedPoint || !selectedPoint.timeSlots) return [];
    return Object.entries(selectedPoint.timeSlots).map(([time, capacity]) => ({
      time,
      capacity,
      available: capacity > 0,
    }));
  };

  const onFinish = async (values) => {
    try {
      setSubmitting(true);
      
      const data = {
        userId: values.userId,
        vaccineId: values.vaccineId,
        vaccinationPointId: values.vaccinationPointId,
        appointmentDate: values.appointmentDate.format('YYYY-MM-DD'),
        timeSlot: values.timeSlot,
        doseNumber: values.doseNumber,
      };

      const res = await appointmentApi.create(data);

      if (res.success) {
        message.success('预约成功');
        navigate('/appointments');
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to create appointment:', error);
      message.error('预约失败');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <h2 className="page-header">新建预约</h2>
      
      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          style={{ maxWidth: 600 }}
        >
          <Form.Item
            name="userId"
            label="选择用户"
            rules={[{ required: true, message: '请选择用户' }]}
          >
            <Select placeholder="请选择用户" showSearch optionFilterProp="children">
              {users.map(user => (
                <Option key={user.id} value={user.id}>
                  {user.name} ({user.age}岁 - {user.phone})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="vaccineId"
            label="选择疫苗"
            rules={[{ required: true, message: '请选择疫苗' }]}
          >
            <Select 
              placeholder="请选择疫苗" 
              onChange={handleVaccineChange}
            >
              {vaccines.map(vaccine => (
                <Option key={vaccine.id} value={vaccine.id}>
                  {vaccine.name} ({vaccine.manufacturer}) - 需{vaccine.requiredDoses}剂
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="doseNumber"
            label="剂次"
            rules={[{ required: true, message: '请选择剂次' }]}
          >
            <Select 
              placeholder="请先选择疫苗" 
              disabled={!selectedVaccineId}
            >
              {getDoseOptions().map(option => (
                <Option key={option.value} value={option.value}>
                  {option.label}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="vaccinationPointId"
            label="选择接种点"
            rules={[{ required: true, message: '请选择接种点' }]}
          >
            <Select 
              placeholder="请选择接种点" 
              onChange={handlePointChange}
            >
              {vaccinationPoints.map(point => (
                <Option key={point.id} value={point.id}>
                  {point.name} - {point.address}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="appointmentDate"
            label="选择日期"
            rules={[{ required: true, message: '请选择日期' }]}
          >
            <DatePicker
              style={{ width: '100%' }}
              placeholder="请选择日期"
              disabledDate={(current) => current && current < dayjs().startOf('day')}
              onChange={handleDateChange}
            />
          </Form.Item>

          <Form.Item
            name="timeSlot"
            label="选择时间段"
            rules={[{ required: true, message: '请选择时间段' }]}
          >
            <Select placeholder="请选择时间段" disabled={!selectedPoint}>
              {getAvailableTimeSlots().map(slot => (
                <Option key={slot.time} value={slot.time} disabled={!slot.available}>
                  {slot.time} (剩余 {slot.capacity} 个名额)
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={submitting}>
                提交预约
              </Button>
              <Button onClick={() => navigate('/appointments')}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default AppointmentCreate;
