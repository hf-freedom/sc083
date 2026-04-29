import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import { Layout, Menu, theme, ConfigProvider } from 'antd';
import {
  HomeOutlined,
  MedicineBoxOutlined,
  CalendarOutlined,
  UserOutlined,
  FileTextOutlined,
  WarningOutlined,
  BellOutlined,
  BarChartOutlined,
  TeamOutlined,
  EnvironmentOutlined,
  UserAddOutlined,
} from '@ant-design/icons';
import Dashboard from './pages/Dashboard';
import VaccineList from './pages/VaccineList';
import VaccineBatchList from './pages/VaccineBatchList';
import AppointmentList from './pages/AppointmentList';
import AppointmentCreate from './pages/AppointmentCreate';
import VaccinationList from './pages/VaccinationList';
import ObservingList from './pages/ObservingList';
import AdverseReactionList from './pages/AdverseReactionList';
import RecallList from './pages/RecallList';
import ReportList from './pages/ReportList';
import UserList from './pages/UserList';
import UserCreate from './pages/UserCreate';
import VaccinationPointList from './pages/VaccinationPointList';
import './App.css';

const { Header, Content, Sider } = Layout;

const menuItems = [
  {
    key: '/dashboard',
    icon: <HomeOutlined />,
    label: '仪表盘',
  },
  {
    key: 'vaccine',
    icon: <MedicineBoxOutlined />,
    label: '疫苗管理',
    children: [
      { key: '/vaccines', icon: <MedicineBoxOutlined />, label: '疫苗列表' },
      { key: '/vaccine-batches', icon: <BarChartOutlined />, label: '批次管理' },
    ],
  },
  {
    key: 'appointment',
    icon: <CalendarOutlined />,
    label: '预约管理',
    children: [
      { key: '/appointments', icon: <CalendarOutlined />, label: '预约列表' },
      { key: '/appointment/create', icon: <CalendarOutlined />, label: '新建预约' },
    ],
  },
  {
    key: 'vaccination',
    icon: <FileTextOutlined />,
    label: '接种管理',
    children: [
      { key: '/vaccinations', icon: <FileTextOutlined />, label: '接种记录' },
      { key: '/observing', icon: <BellOutlined />, label: '留观状态' },
    ],
  },
  {
    key: 'adverse',
    icon: <WarningOutlined />,
    label: '异常反应',
    children: [
      { key: '/adverse-reactions', icon: <WarningOutlined />, label: '反应列表' },
    ],
  },
  {
    key: 'recall',
    icon: <BellOutlined />,
    label: '召回管理',
    children: [
      { key: '/recalls', icon: <BellOutlined />, label: '召回列表' },
    ],
  },
  {
    key: 'report',
    icon: <BarChartOutlined />,
    label: '报表管理',
    children: [
      { key: '/reports', icon: <BarChartOutlined />, label: '报表列表' },
    ],
  },
  {
    key: 'user',
    icon: <UserOutlined />,
    label: '用户管理',
    children: [
      { key: '/users', icon: <TeamOutlined />, label: '用户列表' },
      { key: '/user/create', icon: <UserAddOutlined />, label: '新增用户' },
    ],
  },
  {
    key: '/vaccination-points',
    icon: <EnvironmentOutlined />,
    label: '接种点列表',
  },
];

function AppContent() {
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  const {
    token: { colorBgContainer },
  } = theme.useToken();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <h2 style={{ color: 'white', margin: 0, fontSize: collapsed ? 14 : 18 }}>
            {collapsed ? '💉' : '💉 疫苗预约系统'}
          </h2>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={['vaccine', 'appointment', 'vaccination', 'adverse', 'recall', 'report', 'user']}
          items={menuItems.map(item => ({
            ...item,
            label: item.label ? (
              item.key.startsWith('/') ? (
                <Link to={item.key}>{item.label}</Link>
              ) : item.label
            ) : item.label,
            children: item.children?.map(child => ({
              ...child,
              label: <Link to={child.key}>{child.label}</Link>,
            })),
          }))}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }}>
          <div style={{ padding: '0 24px', fontSize: 16, fontWeight: 500 }}>
            疫苗预约管理系统
          </div>
        </Header>
        <Content style={{ margin: '16px' }}>
          <div className="site-layout-content">
            <Routes>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/" element={<Dashboard />} />
              <Route path="/vaccines" element={<VaccineList />} />
              <Route path="/vaccine-batches" element={<VaccineBatchList />} />
              <Route path="/appointments" element={<AppointmentList />} />
              <Route path="/appointment/create" element={<AppointmentCreate />} />
              <Route path="/vaccinations" element={<VaccinationList />} />
              <Route path="/observing" element={<ObservingList />} />
              <Route path="/adverse-reactions" element={<AdverseReactionList />} />
              <Route path="/recalls" element={<RecallList />} />
              <Route path="/reports" element={<ReportList />} />
              <Route path="/users" element={<UserList />} />
              <Route path="/user/create" element={<UserCreate />} />
              <Route path="/vaccination-points" element={<VaccinationPointList />} />
            </Routes>
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
