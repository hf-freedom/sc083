import React, { useEffect, useState } from 'react';
import { Table, message, Spin, Button, Card, Modal, Descriptions, Tag, Space, DatePicker } from 'antd';
import { reportApi, vaccinationPointApi } from '../api';
import { EyeOutlined, FileAddOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';

function ReportList() {
  const [loading, setLoading] = useState(true);
  const [reports, setReports] = useState([]);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [selectedReport, setSelectedReport] = useState(null);
  const [generateDate, setGenerateDate] = useState(null);
  const [vaccinationPoints, setVaccinationPoints] = useState([]);
  const [selectedPoint, setSelectedPoint] = useState(1);

  useEffect(() => {
    fetchReports();
    fetchVaccinationPoints();
  }, []);

  const fetchVaccinationPoints = async () => {
    try {
      const res = await vaccinationPointApi.getAll();
      if (res.success) {
        setVaccinationPoints(res.data);
      }
    } catch (error) {
      console.error('Failed to fetch vaccination points:', error);
    }
  };

  const fetchReports = async () => {
    try {
      setLoading(true);
      const res = await reportApi.getAll();
      if (res.success) {
        setReports(res.data);
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to fetch reports:', error);
      message.error('获取报表列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = (report) => {
    setSelectedReport(report);
    setDetailModalVisible(true);
  };

  const handleGenerateReport = async () => {
    if (!generateDate) {
      message.warning('请选择日期');
      return;
    }

    try {
      const res = await reportApi.generate(
        generateDate.format('YYYY-MM-DD'),
        selectedPoint
      );

      if (res.success) {
        message.success('报表生成成功');
        fetchReports();
      } else {
        message.error(res.message);
      }
    } catch (error) {
      console.error('Failed to generate report:', error);
      message.error('生成报表失败');
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
      title: '报表日期',
      dataIndex: 'reportDate',
      key: 'reportDate',
    },
    {
      title: '接种点',
      dataIndex: 'vaccinationPointName',
      key: 'vaccinationPointName',
    },
    {
      title: '总预约数',
      dataIndex: 'totalAppointments',
      key: 'totalAppointments',
    },
    {
      title: '已签到',
      dataIndex: 'checkedInCount',
      key: 'checkedInCount',
    },
    {
      title: '已完成',
      dataIndex: 'completedCount',
      key: 'completedCount',
    },
    {
      title: '已取消',
      dataIndex: 'cancelledCount',
      key: 'cancelledCount',
    },
    {
      title: '超时',
      dataIndex: 'timeoutCount',
      key: 'timeoutCount',
    },
    {
      title: '总库存',
      dataIndex: 'totalInventory',
      key: 'totalInventory',
    },
    {
      title: '异常反应数',
      dataIndex: 'adverseReactionCount',
      key: 'adverseReactionCount',
    },
    {
      title: '生成时间',
      dataIndex: 'generatedAt',
      key: 'generatedAt',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => handleViewDetail(record)}
        >
          查看
        </Button>
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
      <h2 className="page-header">报表列表</h2>
      
      <Card style={{ marginBottom: 16 }}>
        <Space>
          <DatePicker
            value={generateDate}
            onChange={setGenerateDate}
            placeholder="选择日期"
            format="YYYY-MM-DD"
          />
          <Button
            type="primary"
            icon={<FileAddOutlined />}
            onClick={handleGenerateReport}
          >
            生成报表
          </Button>
        </Space>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={reports}
          rowKey="id"
          bordered
        />
      </Card>

      <Modal
        title="报表详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={900}
      >
        {selectedReport && (
          <div>
            <Descriptions bordered column={2} title="基本信息">
              <Descriptions.Item label="报表日期">{selectedReport.reportDate}</Descriptions.Item>
              <Descriptions.Item label="接种点">{selectedReport.vaccinationPointName}</Descriptions.Item>
              <Descriptions.Item label="总预约数">{selectedReport.totalAppointments}</Descriptions.Item>
              <Descriptions.Item label="总库存">{selectedReport.totalInventory}</Descriptions.Item>
            </Descriptions>

            <Descriptions bordered column={4} title="预约情况" style={{ marginTop: 16 }}>
              <Descriptions.Item label="已签到">{selectedReport.checkedInCount}</Descriptions.Item>
              <Descriptions.Item label="已完成">{selectedReport.completedCount}</Descriptions.Item>
              <Descriptions.Item label="已取消">{selectedReport.cancelledCount}</Descriptions.Item>
              <Descriptions.Item label="超时">{selectedReport.timeoutCount}</Descriptions.Item>
            </Descriptions>

            {selectedReport.vaccinationByVaccine && Object.keys(selectedReport.vaccinationByVaccine).length > 0 && (
              <div style={{ marginTop: 16 }}>
                <h4>按疫苗统计接种量</h4>
                <Descriptions bordered column={2}>
                  {Object.entries(selectedReport.vaccinationByVaccine).map(([vaccine, count]) => (
                    <Descriptions.Item key={vaccine} label={vaccine}>{count} 剂</Descriptions.Item>
                  ))}
                </Descriptions>
              </div>
            )}

            {selectedReport.inventoryByBatch && Object.keys(selectedReport.inventoryByBatch).length > 0 && (
              <div style={{ marginTop: 16 }}>
                <h4>库存统计</h4>
                <Descriptions bordered column={2}>
                  {Object.entries(selectedReport.inventoryByBatch).map(([batch, count]) => (
                    <Descriptions.Item key={batch} label={batch}>{count} 剂</Descriptions.Item>
                  ))}
                </Descriptions>
              </div>
            )}

            {selectedReport.adverseReactionBySeverity && Object.keys(selectedReport.adverseReactionBySeverity).length > 0 && (
              <div style={{ marginTop: 16 }}>
                <h4>异常反应统计 (共 {selectedReport.adverseReactionCount} 例)</h4>
                <Descriptions bordered column={3}>
                  {Object.entries(selectedReport.adverseReactionBySeverity).map(([severity, count]) => (
                    <Descriptions.Item key={severity} label={severity}>{count} 例</Descriptions.Item>
                  ))}
                </Descriptions>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
}

export default ReportList;
