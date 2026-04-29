import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Button, Card, Space } from 'antd';
import { userApi, vaccinationApi, appointmentApi, adverseReactionApi } from '../api';
import { Link } from 'react-router-dom';
import { UserAddOutlined, EyeOutlined, MedicineBoxOutlined, CalendarOutlined, WarningOutlined } from '@ant-design/icons';

function UserList() {
  const [loading, setLoading] = useState(true);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await userApi.getAll();
      if (res.success) {
        setUsers(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch users:', error);
      message.error('获取用户列表失败');
    } finally {
      setLoading(false);
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
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '身份证号',
      dataIndex: 'idCard',
      key: 'idCard',
    },
    {
      title: '电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
    },
    {
      title: '性别',
      dataIndex: 'gender',
      key: 'gender',
    },
    {
      title: '地址',
      dataIndex: 'address',
      key: 'address',
      ellipsis: true,
    },
    {
      title: '禁忌症',
      key: 'contraindications',
      render: (_, record) => (
        record.contraindications && record.contraindications.length > 0 ? 
          record.contraindications.join(', ') : '无'
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
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
      <h2 className="page-header">用户列表</h2>
      
      <Card style={{ marginBottom: 16 }}>
        <Link to="/user/create">
          <Button type="primary" icon={<UserAddOutlined />}>
            新增用户
          </Button>
        </Link>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={users}
          rowKey="id"
          bordered
        />
      </Card>
    </div>
  );
}

export default UserList;
