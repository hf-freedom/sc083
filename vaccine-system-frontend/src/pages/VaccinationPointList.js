import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Card, Descriptions, Collapse, Tag } from 'antd';
import { vaccinationPointApi, doctorApi } from '../api';

const { Panel } = Collapse;

function VaccinationPointList() {
  const [loading, setLoading] = useState(true);
  const [points, setPoints] = useState([]);
  const [doctorsMap, setDoctorsMap] = useState({});

  useEffect(() => {
    fetchPoints();
  }, []);

  const fetchPoints = async () => {
    try {
      setLoading(true);
      const res = await vaccinationPointApi.getAll();
      if (res.success) {
        setPoints(res.data);
        
        for (const point of res.data) {
          try {
            const doctorsRes = await doctorApi.getByPoint(point.id);
            if (doctorsRes.success) {
              setDoctorsMap(prev => ({
                ...prev,
                [point.id]: doctorsRes.data,
              }));
            }
          } catch (e) {
            console.error(`Failed to fetch doctors for point ${point.id}:`, e);
          }
        }
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch vaccination points:', error);
      message.error('获取接种点列表失败');
    } finally {
      setLoading(false);
    }
  };

  const expandedRowRender = (record) => {
    const doctors = doctorsMap[record.id] || [];
    
    return (
      <div style={{ padding: '0 50px' }}>
        <Collapse defaultActiveKey={['1', '2']}>
          <Panel header="详细信息" key="1">
            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="接种点名称">{record.name}</Descriptions.Item>
              <Descriptions.Item label="地址">{record.address}</Descriptions.Item>
              <Descriptions.Item label="联系电话">{record.phone}</Descriptions.Item>
              <Descriptions.Item label="描述">{record.description}</Descriptions.Item>
              <Descriptions.Item label="每时段最大容量">
                {record.maxCapacityPerTimeSlot} 人
              </Descriptions.Item>
            </Descriptions>
          </Panel>

          <Panel header="可用时间段" key="2">
            {record.timeSlots && Object.entries(record.timeSlots).map(([time, capacity]) => (
              <div key={time} style={{ marginBottom: 8 }}>
                <Tag color={capacity > 0 ? 'green' : 'red'}>
                  {time}: {capacity} 个名额
                </Tag>
              </div>
            ))}
          </Panel>

          <Panel header="医生列表" key="3">
            {doctors.length > 0 ? (
              <Table
                columns={[
                  { title: '姓名', dataIndex: 'name', key: 'name' },
                  { title: '执业证号', dataIndex: 'licenseNumber', key: 'licenseNumber' },
                  { title: '专业', dataIndex: 'specialization', key: 'specialization' },
                  { 
                    title: '状态', 
                    key: 'isAvailable',
                    render: (_, record) => (
                      <Tag color={record.isAvailable ? 'green' : 'red'}>
                        {record.isAvailable ? '可用' : '不可用'}
                      </Tag>
                    )
                  },
                ]}
                dataSource={doctors}
                rowKey="id"
                pagination={false}
                size="small"
              />
            ) : (
              <p>暂无医生</p>
            )}
          </Panel>
        </Collapse>
      </div>
    );
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '接种点名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '地址',
      dataIndex: 'address',
      key: 'address',
      ellipsis: true,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '每时段容量',
      dataIndex: 'maxCapacityPerTimeSlot',
      key: 'maxCapacityPerTimeSlot',
      render: (text) => `${text} 人`,
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
      <h2 className="page-header">接种点列表</h2>
      
      <Card>
        <Table
          columns={columns}
          dataSource={points}
          rowKey="id"
          bordered
          expandable={{ expandedRowRender }}
        />
      </Card>
    </div>
  );
}

export default VaccinationPointList;
