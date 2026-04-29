import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Card, Button, Space, Popconfirm } from 'antd';
import { vaccinationApi } from '../api';
import { CheckCircleOutlined } from '@ant-design/icons';

function ObservingList() {
  const [loading, setLoading] = useState(true);
  const [observingList, setObservingList] = useState([]);

  useEffect(() => {
    fetchObservingList();
    const interval = setInterval(fetchObservingList, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchObservingList = async () => {
    try {
      setLoading(true);
      const res = await vaccinationApi.getObserving();
      if (res.success) {
        setObservingList(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch observing list:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCompleteObservation = async (recordId) => {
    try {
      const res = await vaccinationApi.completeObservation(recordId);
      if (res.success) {
        message.success('留观完成');
        fetchObservingList();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to complete observation:', error);
      message.error(error.response?.data?.message || '完成留观失败');
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
      title: '记录号',
      dataIndex: 'recordNo',
      key: 'recordNo',
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
      title: '留观开始时间',
      dataIndex: 'observationStartTime',
      key: 'observationStartTime',
    },
    {
      title: '留观结束时间',
      dataIndex: 'observationEndTime',
      key: 'observationEndTime',
      render: (text, record) => (
        <Space>
          <span>{text}</span>
          {new Date() > new Date(text) ? (
            <Tag color="green">可离开</Tag>
          ) : (
            <Tag color="orange">留观中</Tag>
          )}
        </Space>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Popconfirm
            title="确定完成留观吗？"
            onConfirm={() => handleCompleteObservation(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="primary"
              size="small"
              icon={<CheckCircleOutlined />}
            >
              完成留观
            </Button>
          </Popconfirm>
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
      <h2 className="page-header">留观状态</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={observingList}
          rowKey="id"
          bordered
          locale={{ emptyText: '暂无留观用户' }}
        />
      </Card>
    </div>
  );
}

export default ObservingList;
