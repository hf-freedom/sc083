import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Card } from 'antd';
import { vaccineApi } from '../api';

function VaccineList() {
  const [loading, setLoading] = useState(true);
  const [vaccines, setVaccines] = useState([]);

  useEffect(() => {
    fetchVaccines();
  }, []);

  const fetchVaccines = async () => {
    try {
      setLoading(true);
      const res = await vaccineApi.getAll();
      if (res.success) {
        setVaccines(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch vaccines:', error);
      message.error('获取疫苗列表失败');
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
      title: '疫苗名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '生产厂家',
      dataIndex: 'manufacturer',
      key: 'manufacturer',
    },
    {
      title: '适用年龄',
      key: 'ageRange',
      render: (_, record) => (
        <span>
          {record.minAge} - {record.maxAge} 岁
        </span>
      ),
    },
    {
      title: '接种间隔',
      key: 'interval',
      render: (_, record) => (
        <span>
          至少 {record.minIntervalDays} 天
        </span>
      ),
    },
    {
      title: '总剂次',
      dataIndex: 'requiredDoses',
      key: 'requiredDoses',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
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
      <h2 className="page-header">疫苗列表</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={vaccines}
          rowKey="id"
          bordered
        />
      </Card>
    </div>
  );
}

export default VaccineList;
