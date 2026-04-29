import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Table, message, Spin, Tag } from 'antd';
import {
  MedicineBoxOutlined,
  TeamOutlined,
  CalendarOutlined,
  FileTextOutlined,
  WarningOutlined,
  BellOutlined,
} from '@ant-design/icons';
import { reportApi, vaccinationApi, appointmentApi } from '../api';

function Dashboard() {
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState(null);
  const [observingList, setObservingList] = useState([]);
  const [recentAppointments, setRecentAppointments] = useState([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      const [dashboardRes, observingRes] = await Promise.all([
        reportApi.getDashboard(1),
        vaccinationApi.getObserving(),
      ]);

      if (dashboardRes.success) {
        setDashboardData(dashboardRes.data);
      } else {
        message.error(dashboardRes.message);
      }

      if (observingRes.success) {
        setObservingList(observingRes.data);
      }
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
      message.error('获取数据失败');
    } finally {
      setLoading(false);
    }
  };

  const statusColumns = [
    {
      title: '用户',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '疫苗',
      dataIndex: 'vaccineName',
      key: 'vaccineName',
    },
    {
      title: '批次',
      dataIndex: 'batchNumber',
      key: 'batchNumber',
    },
    {
      title: '接种点',
      dataIndex: 'vaccinationPointName',
      key: 'vaccinationPointName',
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
      <h2 className="page-header">仪表盘</h2>
      
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={4}>
          <Card className="stat-card">
            <Statistic
              title="用户总数"
              value={dashboardData?.totalUsers || 0}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card className="stat-card">
            <Statistic
              title="疫苗种类"
              value={dashboardData?.totalVaccines || 0}
              prefix={<MedicineBoxOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card className="stat-card">
            <Statistic
              title="可用批次"
              value={dashboardData?.totalBatches || 0}
              prefix={<MedicineBoxOutlined />}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card className="stat-card">
            <Statistic
              title="库存总量"
              value={dashboardData?.totalInventory || 0}
              prefix={<MedicineBoxOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card className="stat-card">
            <Statistic
              title="待处理预约"
              value={dashboardData?.pendingAppointments || 0}
              prefix={<CalendarOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={4}>
          <Card className="stat-card">
            <Statistic
              title="今日接种"
              value={dashboardData?.todayVaccinations || 0}
              prefix={<FileTextOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Card 
            title={<><BellOutlined /> 留观中用户</>} 
            extra={<Tag color="blue">{observingList.length} 人</Tag>}
          >
            <Table
              columns={statusColumns}
              dataSource={observingList}
              rowKey="id"
              size="small"
              pagination={false}
              locale={{ emptyText: '暂无留观用户' }}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card title={<><WarningOutlined /> 待处理异常反应</>}>
            <div style={{ textAlign: 'center', padding: 20 }}>
              <Statistic
                title="待处理数量"
                value={dashboardData?.pendingAdverseReactions || 0}
                valueStyle={{ color: '#cf1322' }}
              />
              <div style={{ marginTop: 16, fontSize: 14, color: '#666' }}>
                请及时查看并处理
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default Dashboard;
