import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Space, Button, Popconfirm, Card } from 'antd';
import { appointmentApi, vaccinationApi } from '../api';
import { CheckOutlined, CloseOutlined, PlayCircleOutlined } from '@ant-design/icons';

function AppointmentList() {
  const [loading, setLoading] = useState(true);
  const [appointments, setAppointments] = useState([]);

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const res = await appointmentApi.getByUser(1);
      if (res.success) {
        setAppointments(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch appointments:', error);
      message.error('获取预约列表失败');
    } finally {
      setLoading(false);
    }
  };

  const getStatusTag = (status) => {
    const statusMap = {
      PENDING: { text: '待处理', color: 'default' },
      LOCKED: { text: '已锁定', color: 'blue' },
      CHECKED_IN: { text: '已签到', color: 'green' },
      COMPLETED: { text: '已完成', color: 'green' },
      CANCELLED: { text: '已取消', color: 'red' },
      TIMEOUT: { text: '超时', color: 'orange' },
    };
    const { text, color } = statusMap[status] || { text: '未知', color: 'default' };
    return <Tag color={color}>{text}</Tag>;
  };

  const handleCheckIn = async (id) => {
    try {
      const res = await appointmentApi.checkIn(id);
      if (res.success) {
        message.success('签到成功');
        fetchAppointments();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to check in:', error);
      message.error('签到失败');
    }
  };

  const handleCancel = async (id) => {
    try {
      const res = await appointmentApi.cancel(id);
      if (res.success) {
        message.success('取消成功');
        fetchAppointments();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to cancel:', error);
      message.error('取消失败');
    }
  };

  const handleStartVaccination = async (appointmentId) => {
    try {
      const res = await vaccinationApi.start(appointmentId);
      if (res.success) {
        message.success('开始接种');
        fetchAppointments();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to start vaccination:', error);
      message.error('开始接种失败');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '预约号',
      dataIndex: 'appointmentNo',
      key: 'appointmentNo',
    },
    {
      title: '用户',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '疫苗名称',
      dataIndex: 'vaccineName',
      key: 'vaccineName',
    },
    {
      title: '批次号',
      dataIndex: 'batchNumber',
      key: 'batchNumber',
    },
    {
      title: '接种点',
      dataIndex: 'vaccinationPointName',
      key: 'vaccinationPointName',
    },
    {
      title: '医生',
      dataIndex: 'doctorName',
      key: 'doctorName',
    },
    {
      title: '预约日期',
      dataIndex: 'appointmentDate',
      key: 'appointmentDate',
    },
    {
      title: '时间段',
      dataIndex: 'timeSlot',
      key: 'timeSlot',
    },
    {
      title: '剂次',
      dataIndex: 'doseNumber',
      key: 'doseNumber',
    },
    {
      title: '状态',
      key: 'status',
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          {record.status === 'LOCKED' && (
            <>
              <Button
                type="primary"
                size="small"
                icon={<CheckOutlined />}
                onClick={() => handleCheckIn(record.id)}
              >
                签到
              </Button>
              <Popconfirm
                title="确定要取消预约吗？"
                onConfirm={() => handleCancel(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button danger size="small" icon={<CloseOutlined />}>
                  取消
                </Button>
              </Popconfirm>
            </>
          )}
          {record.status === 'CHECKED_IN' && (
            <Button
              type="primary"
              size="small"
              icon={<PlayCircleOutlined />}
              onClick={() => handleStartVaccination(record.id)}
            >
              开始接种
            </Button>
          )}
        </Space>
      ),
    },
  ];

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <h2 className="page-header">预约列表</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={appointments}
          rowKey="id"
          bordered
        />
      </Card>
    </div>
  );
}

export default AppointmentList;
