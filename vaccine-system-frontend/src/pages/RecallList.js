import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Tag, Space, Button, Card, Modal, Descriptions, List } from 'antd';
import { recallApi } from '../api';
import { BellOutlined, EyeOutlined } from '@ant-design/icons';

function RecallList() {
  const [loading, setLoading] = useState(true);
  const [recalls, setRecalls] = useState([]);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedRecall, setSelectedRecall] = useState(null);
  const [affectedUsers, setAffectedUsers] = useState([]);

  useEffect(() => {
    fetchRecalls();
  }, []);

  const fetchRecalls = async () => {
    try {
      setLoading(true);
      const res = await recallApi.getAll();
      if (res.success) {
        setRecalls(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch recalls:', error);
      message.error('获取召回列表失败');
    } finally {
      setLoading(false);
    }
  };

  const getLevelTag = (level) => {
    const colorMap = {
      '1级': 'red',
      '2级': 'orange',
      '3级': 'blue',
    };
    return <Tag color={colorMap[level] || 'default'}>{level}</Tag>;
  };

  const handleViewDetail = async (recall) => {
    setSelectedRecall(recall);
    setDetailModalVisible(true);
    
    try {
      const res = await recallApi.getAffectedUsers(recall.vaccineBatchId);
      if (res.success) {
        setAffectedUsers(res.data);
      }
    } catch (error) {
      console.error('Failed to fetch affected users:', error);
    }
  };

  const handleMarkNotified = async (id) => {
    try {
      const res = await recallApi.markNotified(id);
      if (res.success) {
        message.success('已标记为已通知');
        fetchRecalls();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to mark as notified:', error);
      message.error('标记失败');
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
      title: '通知编号',
      dataIndex: 'noticeNo',
      key: 'noticeNo',
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
      title: '召回原因',
      dataIndex: 'recallReason',
      key: 'recallReason',
      ellipsis: true,
    },
    {
      title: '召回级别',
      key: 'recallLevel',
      render: (_, record) => getLevelTag(record.recallLevel),
    },
    {
      title: '影响人数',
      dataIndex: 'affectedCount',
      key: 'affectedCount',
    },
    {
      title: '通知状态',
      key: 'isNotified',
      render: (_, record) => (
        record.isNotified ? 
          <Tag color="green">已通知</Tag> : 
          <Tag color="orange">未通知</Tag>
      ),
    },
    {
      title: '发布时间',
      dataIndex: 'issueTime',
      key: 'issueTime',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            查看
          </Button>
          {!record.isNotified && (
            <Button
              type="link"
              size="small"
              icon={<BellOutlined />}
              onClick={() => handleMarkNotified(record.id)}
            >
              标记通知
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
      <h2 className="page-header">召回列表</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={recalls}
          rowKey="id"
          bordered
        />
      </Card>

      <Modal
        title="召回通知详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={800}
      >
        {selectedRecall && (
          <div>
            <Descriptions bordered column={2}>
              <Descriptions.Item label="通知编号">{selectedRecall.noticeNo}</Descriptions.Item>
              <Descriptions.Item label="疫苗名称">{selectedRecall.vaccineName}</Descriptions.Item>
              <Descriptions.Item label="批次号">{selectedRecall.batchNumber}</Descriptions.Item>
              <Descriptions.Item label="召回级别">{selectedRecall.recallLevel}</Descriptions.Item>
              <Descriptions.Item label="影响人数">{selectedRecall.affectedCount}</Descriptions.Item>
              <Descriptions.Item label="通知状态">
                {selectedRecall.isNotified ? '已通知' : '未通知'}
              </Descriptions.Item>
              <Descriptions.Item label="发布时间" span={2}>
                {selectedRecall.issueTime}
              </Descriptions.Item>
              <Descriptions.Item label="召回原因" span={2}>
                {selectedRecall.recallReason}
              </Descriptions.Item>
            </Descriptions>

            <div style={{ marginTop: 24 }}>
              <h3>受影响用户列表</h3>
              {affectedUsers.length > 0 ? (
                <List
                  bordered
                  dataSource={affectedUsers}
                  renderItem={(user) => (
                    <List.Item>
                      <List.Item.Meta
                        title={user.name}
                        description={`电话: ${user.phone} | 年龄: ${user.age}岁`}
                      />
                    </List.Item>
                  )}
                />
              ) : (
                <p>暂无受影响用户</p>
              )}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}

export default RecallList;
